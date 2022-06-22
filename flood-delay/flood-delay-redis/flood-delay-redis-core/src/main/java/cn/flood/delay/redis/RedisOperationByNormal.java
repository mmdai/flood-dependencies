package cn.flood.delay.redis;

import cn.flood.delay.entity.DelayQueueJob;
import cn.flood.delay.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description redis的操作类; 线程不安全; 不可使用
 * @Author daimi
 * @Date 2019/8/1 3:12 PM
 **/
@SuppressWarnings("unchecked")
public class RedisOperationByNormal implements RedisOperation {

    private static final Logger logger = LoggerFactory.getLogger(RedisOperationByNormal.class);

    protected RedisTemplate redisTemplate;

    public RedisOperationByNormal(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    //新增一个Job
    @Override
    public void addJob(String topic, DelayQueueJob arg, long runTimeMillis){
        String topicId = RedisKeyUtil.getTopicId(topic,arg.getId());
        //Job池放入一个Job
        redisTemplate.opsForHash().put(RedisKeyUtil.getDelayQueueTableKey(),topicId, arg);
        //Bucket放入Job的时间戳
        redisTemplate.opsForZSet().add(RedisKeyUtil.getBucketKey(), topicId, runTimeMillis);
        logger.info("新增成功Job:topicId =>{}",topicId);
    }

    @Override
    public void retryJob(String topic, String id, Object content){
        String topicId = RedisKeyUtil.getTopicId(topic,id);
        //Job池放入一个Job
        redisTemplate.opsForHash().put(RedisKeyUtil.getDelayQueueTableKey(),topicId,content);
        redisTemplate.opsForList().leftPush(RedisKeyUtil.getTopicListKey(topic),topicId);
        logger.info("新增重试Job成功:topicId =>{}",topicId);
    }

    //删除一个Job
    @Override
    public void deleteJob(String topic, String id){
        String topicId = RedisKeyUtil.getTopicId(topic,id);
        redisTemplate.opsForHash().delete(RedisKeyUtil.getDelayQueueTableKey(),topicId);
        redisTemplate.opsForZSet().remove(RedisKeyUtil.getBucketKey(),topicId);
        logger.info("删除成功Job:topicId =>{}",topicId);
    }



    /**
     * 这个方法线程不安全  rang获取 和 remove不是原子操作
     * 并且每一次搬运都是一次请求，增加了网络开销
     * 从zset搬运到list
     * 做一次搬运操作并且返回搬运完之后的 队首元素的score
     * 如果搬运之后没有了元素则返回Long.MAX_VALUE
     * @return
     */
    @Override
    public long moveAndRtTopScore(){
        int maxCount = 1000;
        for(int i=0;i<maxCount;i++){
            Set<String> members = redisTemplate.opsForZSet().range(RedisKeyUtil.getBucketKey(),0L,1L);
            if(members==null||members.size()==0){
                return Long.MAX_VALUE;
            }
            Iterator it1 = members.iterator();
            if(!it1.hasNext()){
                return Long.MAX_VALUE;
            }
            Object member = it1.next();
            Double score = redisTemplate.opsForZSet().score(RedisKeyUtil.getBucketKey(),member);
            if(score<=System.currentTimeMillis()){
                //move
                redisTemplate.opsForZSet().remove(RedisKeyUtil.getBucketKey(), member);
                logger.info("{}删除元素{}",RedisKeyUtil.getBucketKey(),member);
                redisTemplate.opsForList().leftPush(RedisKeyUtil.getTopicListKeyByMember(member.toString()),member);
                logger.info("List队列{}push一个新元素{}",RedisKeyUtil.getTopicListKeyByMember(member.toString()),member);
            }
        }
        //最后查一次返回
        //
        Set<String> ms2 = redisTemplate.opsForZSet().range(RedisKeyUtil.getBucketKey(),0L,1L);
        Iterator it2 = ms2.iterator();
        if(!it2.hasNext()){
            return Long.MAX_VALUE;
        }
        Object m2 = it2.next();
        Double score2 = redisTemplate.opsForZSet().score(RedisKeyUtil.getBucketKey(),m2);
        if(score2==null){
            return Long.MAX_VALUE;
        }
        return score2.longValue();
    }

    //阻塞获取List中的元素
    @Override
    public Object BLPOP(String topic){
        String topicId = BLPOPKey(topic);
        if(topicId == null){
            return null;
        }
        return getJob(topicId);
    }

    /**
     * 阻塞超时会返回null
     * n秒之内没有获取到数据则断开连接
     * @param topic
     * @return
     */
    @Override
    public String BLPOPKey(String topic){
        Object object =  BLPOP(RedisKeyUtil.getTopicListKey(topic),5*60*1000);
        if(object==null){
            return null;
        }
        return object.toString();
    }
    @Override
    public String BLPOP(String key,long timeout){
        Object object = redisTemplate.opsForList().leftPop(key,timeout,TimeUnit.MILLISECONDS);
        if(object==null){
            return null;
        }
        return object.toString();
    }

    @Override
    public List<String> lrangeAndLTrim(String topic, int maxGet) {
        return null;
    }

    /****
     *     查询REDIS_DELAY_TABLE 中的Job详情
     */
    @Override
    public DelayQueueJob getJob(String topicId){
        Object args =  redisTemplate.opsForHash().get(RedisKeyUtil.getDelayQueueTableKey(),topicId);
        if(args == null){
            return null;
        }
        return (DelayQueueJob)args;
    }


    @Override
    public void rPush(String topicId) {
        redisTemplate.opsForList().rightPush(RedisKeyUtil.getTopicListKeyByMember(topicId), topicId);
    }
    @Override
    public List<RedisClientInfo> getThisMachineAllBlpopClientList(){
        List<RedisClientInfo> list  =  redisTemplate.getClientList();
        return list;
    }

    @Override
    public void killClient(List<String> clients) {
        clients.forEach((a)->{
            String address[] = a.split(":");
            redisTemplate.killClient(address[0],Integer.parseInt(address[1]));
        });
    }

}
