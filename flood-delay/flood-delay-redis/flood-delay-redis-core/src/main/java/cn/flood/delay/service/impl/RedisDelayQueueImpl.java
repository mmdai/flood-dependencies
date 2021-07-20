package cn.flood.delay.service.impl;

import cn.flood.delay.common.DelayQueueException;
import cn.flood.delay.common.GlobalConstants;
import cn.flood.delay.entity.DelayQueueJob;
import cn.flood.delay.mapper.TbDelayJobMapper;
import cn.flood.delay.redis.RedisOperation;
import cn.flood.delay.utils.NextTimeHolder;
import cn.flood.delay.service.RedisDelayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @Description 提供给客户端使用的 延迟队列操作
 * @Author daimi
 * @Date 2019/7/30 5:33 PM
 **/
public class RedisDelayQueueImpl implements RedisDelayQueue {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedisOperation redisOperation;
    private ConcurrentHashMap<String, AbstractTopicRegister> topicRegisterHolder;

    private ExecutorService executor;

    private TbDelayJobMapper tbDelayJobMapper;


    public RedisDelayQueueImpl(RedisOperation redisOperation, ConcurrentHashMap<String, AbstractTopicRegister> topicRegisterHolder,
                               ExecutorService executor, TbDelayJobMapper tbDelayJobMapper) {
        this.redisOperation = redisOperation;
        this.topicRegisterHolder = topicRegisterHolder;
        this.executor = executor;
        this.tbDelayJobMapper = tbDelayJobMapper;
    }


    @Override
    public void add(DelayQueueJob args, String topic, long runTimeMillis) {
        executor.execute(()->addJob(args, topic, runTimeMillis));
    }

    @Override
    public void add(DelayQueueJob args, long delayTimeMillis, String topic){
        executor.execute(()->addJob(args, delayTimeMillis, topic));
    }

    private void addJob(DelayQueueJob args, long delayTimeMillis, String topic) {
        long runTimeMillis = args.getCreateTime() + delayTimeMillis;
        args.setTopic(topic);
        args.setDelay(delayTimeMillis);
        args.setExecutionTime(runTimeMillis);
        preCheck(args,topic,null,delayTimeMillis);
        redisOperation.addJob(topic, args, runTimeMillis);
        //尝试更新下次的执行时间
        NextTimeHolder.tryUpdate(runTimeMillis);
        //add 添加delay入库
        if(null != tbDelayJobMapper ){
            args.setCreateDate(LocalDateTime.now());
            args.setUpdateDate(LocalDateTime.now());
            tbDelayJobMapper.insert(args);
        }
    }

    private void addJob(DelayQueueJob args, String topic, long runTimeMillis) {
        long delayTimeMillis = runTimeMillis - args.getCreateTime();
        args.setTopic(topic);
        args.setDelay(delayTimeMillis);
        args.setExecutionTime(runTimeMillis);
        preCheck(args, topic, runTimeMillis,null);
        redisOperation.addJob(topic, args, runTimeMillis);
        //尝试更新下次的执行时间
        NextTimeHolder.tryUpdate(runTimeMillis);
        //add 添加delay入库
        if(null != tbDelayJobMapper ){
            args.setCreateDate(LocalDateTime.now());
            args.setUpdateDate(LocalDateTime.now());
            tbDelayJobMapper.insert(args);
        }
    }

    private void preCheck(DelayQueueJob args, String topic, Long runTimeMillis, Long delayTimeMillis) {
        if(checkStringEmpty(topic) ||
                checkStringEmpty(args.getId())){
            throw new DelayQueueException("未设置Topic或者Id!");
        }
        if(runTimeMillis == null){
            if(delayTimeMillis == null){
                throw new DelayQueueException("未设置延迟执行时间!");
            }
        }
        if(topic.contains(":")){
            throw new DelayQueueException("Topic 不能包含特殊字符 :  !");
        }
        //check topic exist
        if(!checkTopicExist(topic)){
            throw new DelayQueueException("Topic未注册!");
        }
    }

    @Override
    public void delete(String topic, String id) {
        executor.execute(()->redisOperation.deleteJob(topic, id));
        //add 添加delay入库
        if(null != tbDelayJobMapper ){
            DelayQueueJob delay = new DelayQueueJob();
            delay.setId(id);
            delay.setStatus(GlobalConstants.STATUS_DELETED);
            delay.setUpdateDate(LocalDateTime.now());
            tbDelayJobMapper.update(delay);
        }
        logger.info("删除延时任务:Topic:{},id：{}",topic,id);
    }

    private boolean checkStringEmpty(String string){
        return string==null || string.length()==0;
    }

    public  boolean checkTopicExist(String topic){
        for(Map.Entry<String, AbstractTopicRegister> entry: topicRegisterHolder.entrySet()) {
            if(entry.getKey().equals(topic)){
                return true;
            }
        }
        return false;
    }
}
