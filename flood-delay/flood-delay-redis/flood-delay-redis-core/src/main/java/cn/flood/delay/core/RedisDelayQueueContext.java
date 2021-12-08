package cn.flood.delay.core;

import cn.flood.delay.common.DelayQueueException;
import cn.flood.delay.common.GlobalConstants;
import cn.flood.delay.entity.DelayQueueJob;
import cn.flood.delay.mapper.TbDelayJobMapper;
import cn.flood.delay.service.RedisDelayQueue;
import cn.flood.delay.service.impl.AbstractTopicRegister;
import cn.flood.delay.service.impl.RedisDelayQueueImpl;
import cn.flood.delay.redis.RedisOperation;
import cn.flood.delay.redis.RedisOperationByLua;
import cn.flood.delay.threads.Move2ReadyThread;
import cn.flood.delay.threads.RetryOutTimesThread;
import cn.flood.delay.threads.ShutdownThread;
import cn.flood.delay.utils.*;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.util.ObjectUtils;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * @Description redis延迟队列 核心类
 * @Author daimi
 * @Date 2019/7/31 9:16 AM
 **/
public  class RedisDelayQueueContext   {

    private static final Logger logger = LoggerFactory.getLogger(RedisDelayQueueContext.class);

    /**redis延迟队列对外提供接口**/
    private RedisDelayQueue redisDelayQueue;

    /**mapper接口**/
    private TbDelayJobMapper tbDelayJobMapper;

    /**项目名: 怕不同项目之间共用一个redis实例;避免Topic重复;加上项目名前缀**/
    public static String PROJECTNAME ;

    /**保存所有的Topic的回调接口**/
    private static ConcurrentHashMap<String, AbstractTopicRegister> topicRegisterHolder = new ConcurrentHashMap<>();

    /**redis操作类**/
    private RedisOperation redisOperation;

    /**当前在Redis服务器那里的Ip;一般redis跟服务都在同一内网;如果是外网Ip需要自己手动设置;根据这个Ip来杀掉redis的clients  **/
    private  volatile String ipInRedisServer;


    /**TOPIC消费线程 标志位**/
    private static volatile boolean topicThreadStop = false;

    /**是否能够使用BLPOP**/
    private static boolean canUseBlpop = false;

    /**将异常情况下导致未被消费的Job 重新放回 待消费列表LIST中**/
    private  ThreadPoolExecutor RPUSH_NO_EXEC_JOB = new ThreadPoolExecutor(
            0,
            50,
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("异常未消费重入"+"-%d").build(),
            //队列满了 丢掉任务不抛出异常;
            new ThreadPoolExecutor.DiscardPolicy()
    );


    /**redisDelayQueue: 接口的异步调用 线程池;**/
    private static ExecutorService DelayQ_ASYNC = new ThreadPoolExecutor(10,
            10, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());






    /**
     * 保证机制: 避免某台持有最小nextTime的机器挂掉;而导致不能准时消费
     * 唤醒线程: 每分钟唤醒一次  搬运线程
     * **/
    private  final ScheduledExecutorService TIMER_NOTIFY = Executors.newScheduledThreadPool(1);


    public RedisDelayQueueContext(RedisTemplate<Object, Object> redisTemplate, String projectName) {
        initCtx(redisTemplate, projectName,null, null);
    }
    public RedisDelayQueueContext(RedisTemplate<Object, Object> redisTemplate, String projectName, TbDelayJobMapper tbDelayJobMapper) {
        initCtx(redisTemplate, projectName,null, tbDelayJobMapper);
    }

    public RedisDelayQueueContext(RedisTemplate<Object, Object> redisTemplate, String projectName, String ipInRedisServer) {
        initCtx(redisTemplate,projectName,ipInRedisServer, null);
    }

    final protected void initCtx(RedisTemplate<Object, Object> redisTemplate, String projectName,String ipInRedisServer,
                                 TbDelayJobMapper tbDelayJobMapper){
        if(projectName.contains(":")||projectName.contains("{")||projectName.contains("}")){
            throw new DelayQueueException("projectName 不能包含特殊字符 : } { ");
        }
        this.redisOperation = new RedisOperationByLua(redisTemplate);
        this.redisDelayQueue = new RedisDelayQueueImpl(redisOperation, topicRegisterHolder, DelayQ_ASYNC, tbDelayJobMapper);
        this.ipInRedisServer = ipInRedisServer;
        this.tbDelayJobMapper = tbDelayJobMapper;
        PROJECTNAME = projectName;
        init();
    }


    private void init(){
        //启动监听Bucket线程
        Move2ReadyThread.getInstance().runMove2ReadyThread(redisOperation);
        //支持BLPOP吗
        checkBlpop();
        //5秒后启动Topic的监听线程池
        runTopicsThreadAfter5Sec();
        //启动每分钟唤醒一次线程
        runTimerNotify();
        //注册 优雅关机
        registerDestory();
    }

    //检查是否能够支持 BLPOP的操作;  codis 等集群不支持BLPOP
    private void checkBlpop(){
        try {
             redisOperation.BLPOP("daimi_11&*",5000);
             canUseBlpop = true;
        }catch (Exception e){
            // nested exception is io.lettuce.core.RedisCommandTimeoutException
            //如果Redis的客户端用的是 lettuce；好像阻塞的情况下超时之后会抛异常
            canUseBlpop = false;
        }
    }


    //注册优雅停机
    private void registerDestory(){
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("延迟任务开始关机....");
            //关闭异步AddJob线程池
            ShutdownThread.closeExecutor(DelayQ_ASYNC,"异步AddJob线程池");
            //停止唤醒线程
            TIMER_NOTIFY.shutdown();
            //停止搬运线程
            Move2ReadyThread.getInstance().toStop();
            //停止topic监听redis线程和 topic消费线程
            shutdownTopicThreads();
            //停止失败重试线程池
            RetryOutTimesThread.getInstance().toStop();
            //停止 异常未消费JOb重入List线程池
            ShutdownThread.closeExecutor(RPUSH_NO_EXEC_JOB,"异常未消费重入List线程池");
            logger.info("延迟任务关机完毕....");
        }));
    }

    /**
     * 5秒后启动注册 TOPIC消息线程;
     * 为啥不直接启动,因为AbstractTopicRegister有可能还没有被注册;所以这里等5秒再去(应该差不多注册完了吧)
     * TODO:有啥更好的方法? 就是想让AbstractTopicRegister注册完了再启动;
     * Spring是有等所有Bean加载完了再调用方法;但是我不想把这种事情放到业务方去做;
     * **/
    private void runTopicsThreadAfter5Sec(){
        new Thread(()->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runTopicsThreads();
        }).start();
    }





    /**
     * 每分钟触发一次搬运操作(这个只是宕机保证最坏情况延迟一分钟执行;)
     */
    private void runTimerNotify() {
        TIMER_NOTIFY.scheduleWithFixedDelay(()-> NextTimeHolder.setZeroAndNotify(),1,1,TimeUnit.MINUTES);
    }




    /**
     * 启动消费TOPIC_LIST的线程
     * 每一个TOPIC对应一个线程池;   每个线程池设置的 核心线程数都用阻塞原语BLPOP去阻塞获取LIST的元素;
     * 一个核心线程消费一个元素;不使用线程池的阻塞队列;避免服务器宕机之后这些任务丢失; 阻塞队列就是用的redis的List
     * @失败重试:
     *      1. 业务方的回调接口失败异常会重试
     *          默认重试2次,总共最多消费3次; 需要重试的Job会放在List的队尾中等待消费;
     *          业务方也可以设置  retryCount = -1 ;来阻止重试
     *      2. BLPOP成功,但是获取Job的时候连接超时或者异常
     *         这种不算回调接口异常,BLPOP已经删除了元素,所有需要重新放回到待消费列表
     */
    private void runTopicsThreads(){
        //初始化Threads-topic
        logger.info("初始化Topic线程runTopicsThreads topic.size:{}",topicRegisterHolder.size());
        for(Map.Entry<String, AbstractTopicRegister> entry: topicRegisterHolder.entrySet()) {
            AbstractTopicRegister register = entry.getValue();
            runTopicThreads(register);
        }
    }

    private void runTopicThreads(AbstractTopicRegister register) {

            //同时执行的线程数量不能超过register.getMaxPoolSize()数量,
            // 因为不打算将任务放入到线程池的阻塞队列,直接用redis的list当做阻塞队列,防止宕机丢失任务
            Semaphore semaphore = new Semaphore(register.getMaxPoolSize());
            Thread a = new Thread(()->{
                logger.info("创建Topic:{}线程;topicThreadStop:{}",register.getTopic(), topicThreadStop);
                while (!topicThreadStop){
                    try {
                        //可用许可证数量
                        int availablePermits = semaphore.availablePermits();
                        if(availablePermits==0){
                            //如果当前可用的许可不够 阻塞获取一个信号量;这里就是用来当做阻塞的功能
                            semaphore.acquire(1);
                            semaphore.release();
                        }
                        int maxGet = register.getLrangMaxCount()<availablePermits?register.getLrangMaxCount():availablePermits;
                        List<String> topicIds ;
                        if(maxGet<=1||
                                (null ==(topicIds=redisOperation.lrangeAndLTrim(register.getTopic(), maxGet))
                                        ||topicIds.size()==0 )){
                            topicIds = new ArrayList<>(1);

                            if(canUseBlpop){
                                // 如果是用codis实现集群的话,codis不支持BLPOP的操作！
                                //https://github.com/CodisLabs/codis/issues/841
                                long blpop = Clock.systemDefaultZone().millis();
                                String topicId = redisOperation.BLPOPKey(register.getTopic());
                                logger.info("BLPOPKey 耗时:{}",Clock.systemDefaultZone().millis()-blpop);
                                if(topicId!=null){
                                    topicIds.add(topicId);
                                }
                            }else {
                                // codis和twemproxy等等集群 不支持BLPOP 改成每秒获取一次
                                Thread.sleep(1000);
                            }
                        }
                        if(topicIds!=null&&topicIds.size()>0){
                            //获取许可
                            semaphore.acquire(topicIds.size());
                            for(int i =0; i<topicIds.size(); i++){
                                if(ObjectUtils.isEmpty(topicIds.get(i)))continue;
                                String topicId = topicIds.get(i).replaceAll("\"","");
                                register.getTOPIC_THREADS().execute(()->{
                                    boolean isfail = false;
                                    DelayQueueJob args = null;

                                    try {
                                        args = redisOperation.getJob(topicId + "");
                                        if (args != null) {
                                            //延时执行时间
                                            args.setTtr(Clock.systemDefaultZone().millis() - args.getExecutionTime());
                                            try {
                                                checkTimeoutExectue(register.getMethodTimeout(), register, args);
                                                if(args.getRetryCount()>0){
                                                    logger.info("重试延迟任务第{}次重试消费成功,topicId:{},DelayQueueJob:{} ",args.getRetryCount(), RedisKeyUtil.getTopicId(register.getTopic(),args.getId()),args.toString());
                                                }else {
                                                    logger.info("延迟任务消费成功,topicId:{},DelayQueueJob:{} ",RedisKeyUtil.getTopicId(register.getTopic(),args.getId()),args.toString());
                                                }
                                            }catch (Exception e){
                                                if(args.getRetryCount()>0){
                                                    logger.error("重试任务第{}次重试败,执行回调接口出错:topicId:{},DelayQueueJob:{},Err:{}",args.getRetryCount(),RedisKeyUtil.getTopicId(register.getTopic(),args.getId()),args.toString(), ExceptionUtil.getStackTrace(e));
                                                }else {
                                                    logger.error("延迟任务消费失败,执行回调接口出错:topicId:{},DelayQueueJob:{},Err:{}",RedisKeyUtil.getTopicId(register.getTopic(),args.getId()),args.toString(),ExceptionUtil.getStackTrace(e));
                                                }
                                                isfail = true;
                                                // 重试流程
                                                RetryOutTimesThread.getInstance().callBackExceptionTryRetry(register, args,redisOperation);
                                            }
                                        }
                                    }catch (Exception e){
                                        /**topicId被Pop出来了,但是Job没有消费;都需要重新放入到队列中**/
                                        if(args==null){
                                            logger.error("延迟任务消费异常: TopicId已经Pop出来,但是Job没有被消费,重新RightPush进待消费列表;topicId:{},Err:{}",
                                                    topicId,ExceptionUtil.getStackTrace(e));
                                            againRightPush(register.getTopic(), args);
                                            if(null != tbDelayJobMapper){
                                                args.setStatus(GlobalConstants.STATUS_RUNNER);
                                                args.setUpdateDate(LocalDateTime.now());
                                                tbDelayJobMapper.update(args);
                                            }
                                        }
                                    }finally {
                                        /**1.执行成功 删除  **/
                                        if(args != null && !isfail){
                                            //消费能力跟不上,这里可能会连接redis超时导致这里的Job没有被删除,但是影响不大
                                            redisOperation.deleteJob(register.getTopic(),args.getId());
                                            if(null != tbDelayJobMapper){
                                                args.setStatus(GlobalConstants.STATUS_SUCCESS);
                                                args.setUpdateDate(LocalDateTime.now());
                                                tbDelayJobMapper.update(args);
                                            }
                                        }
                                        /**  2.就算执行失败,但是设置了不重试 也删除
                                          *  3.就算执行失败,但是已经重试了2次 还是删除**/
                                        if(args != null  && ((args.getRetryCount()>2||args.getRetryCount()<0) ) ){
                                            if(null != tbDelayJobMapper){
                                                args.setStatus(GlobalConstants.STATUS_ERROR);
                                                args.setUpdateDate(LocalDateTime.now());
                                                tbDelayJobMapper.update(args);
                                            }
                                            //消费能力跟不上,这里可能会连接redis超时导致这里的Job没有被删除,但是影响不大
                                            redisOperation.deleteJob(register.getTopic(),args.getId());
                                        }
                                        semaphore.release();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        logger.error(ExceptionUtil.getStackTrace(e));
                        try {
                            //避免redis断开连接一直刷屏,睡一秒
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            });
            a.setDaemon(true);
            a.setName(register.getTopic()+"监听redis线程");
            a.start();
    }

    private void checkTimeoutExectue(final long timeout, AbstractTopicRegister register, DelayQueueJob args) throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutUtil.timeoutMethod(timeout,(a)->{
             register.execute(args);
             return false;
        });
    }


    /**
     *  如果是redis连接超时;会出现这样的问题
     * 1.BLPOPKey 阻塞pop成功了； List中元素已经没有了
     * 2.通过这个pop出来的元素去 Jobs_Tables读取content;但是这个读取的时候连接超时了
     * 那么这个JOb就没有被消费了;所有这个时候要重新push到List中;
     */
    private void againRightPush(String topic, DelayQueueJob args) {
        if(args.getReentry()>2){
            logger.error("未被消费任务topicId:{},重入了3次仍旧失败",RedisKeyUtil.getTopicId(topic,args.getId()));
        }else {
            RPUSH_NO_EXEC_JOB.execute(()->{
                //获取JOb的方法连接失败了 异步重新放入;
                logger.warn("未被消费任务topicId:{} 重新放入待消费队列",RedisKeyUtil.getTopicId(topic,args.getId()));
                args.setRetryCount(args.getReentry()+1);
                redisOperation.retryJob(topic,args.getId(),args);
            });
        }

    }


    final public static void addTopic(String topic,AbstractTopicRegister register){
        if(null!= topicRegisterHolder.get(topic)){
            throw new DelayQueueException("Topic 注册重复,请保证Topic唯一");
        }
        topicRegisterHolder.put(topic, register);
    }


    public RedisDelayQueue getRedisDelayQueue() {
        return redisDelayQueue;
    }






    public static void setTopicThreadStop(boolean topicThreadStop) {
        RedisDelayQueueContext.topicThreadStop = topicThreadStop;
    }



    /**
     * 等10秒再强制停止线程
     * 如果回调函数特别耗费时间,或者BLPOP刚好在第9秒获取到了元素,那么就只留给回调函数1秒;
     * 很有可能执行不完;
     *
     * 解决方法 ①、  可以把BLOPO连接 的超时时间设置为5秒; 根据状态位tostop
     * 就不会再继续BLPOP取数据了,最坏情况留给回调函数5秒执行完够了;
     * 但是这样又会带入一个新的问题;   就是如果消息队列一直没有消息;  那么TOPIC的消费线程池就会
     * 每隔5秒创建一次连接然后关闭;  这样太浪费系统资源了;
     *
     * 解决方案 ②、立刻断开开那些BLOPO的客户端;
     *             但是这样可能误杀其他服务的BLPOP连接(问题不大)
     *
     * 如果是一个线程跟redis连接 那选方案一  如果是用很多线程跟redis保持长连接 那选方案二
     */
    private void shutdownTopicThreads(){
        RedisDelayQueueContext.setTopicThreadStop(true);
        //killThisMachineAllRedisBlpopClients(redisOperation);
        for(Map.Entry<String, AbstractTopicRegister> entry: topicRegisterHolder.entrySet()) {
            ShutdownThread.closeExecutor(entry.getValue().getTOPIC_THREADS(),entry.getKey());
        }
    }
    private  void killThisMachineAllRedisBlpopClients(RedisOperation redisOperation) {
        String ip = ipInRedisServer;
        if(ObjectUtils.isEmpty(ip)){
            ip = NetUtil.getLocalHostLANAddress();
        }
        logger.info("ipInRedisServer;{}",ip);
        List<RedisClientInfo> list = redisOperation.getThisMachineAllBlpopClientList();
        List<String> kills = Lists.newArrayList();
        for(RedisClientInfo info:list){
            if(info.getAddressPort().split(":")[0].equals(ip)
                    &&info.getLastCommand().equals("blpop")){
                kills.add(info.getAddressPort());
                logger.info("优雅关机,杀掉redis的Blpop客户端;{}",info.getAddressPort());

            }
        }
        redisOperation.killClient(kills);
    }



}
