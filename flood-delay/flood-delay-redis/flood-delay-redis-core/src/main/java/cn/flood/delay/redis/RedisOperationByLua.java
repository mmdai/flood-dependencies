package cn.flood.delay.redis;

import cn.flood.delay.utils.ExceptionUtil;
import cn.flood.delay.utils.RedisKeyUtil;
import cn.flood.delay.entity.DelayQueueJob;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.ObjectUtils;


import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description redis的操作类,不过都是执行的Lua脚本
 * @Author daimi
 * @Date 2019/8/1 3:20 PM
 **/
@SuppressWarnings("unchecked")
public class RedisOperationByLua extends RedisOperationByNormal{

    private static final Logger logger = LoggerFactory.getLogger(RedisOperationByLua.class);

    public RedisOperationByLua(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }


    @Override
    public void addJob(String topic, DelayQueueJob arg, long runTimeMillis) {

        List<String> keys = Lists.newArrayList();
        keys.add(RedisKeyUtil.getDelayQueueTableKey());
        keys.add(RedisKeyUtil.getBucketKey());
        DefaultRedisScript redisScript =new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/addJob.lua")));
        redisTemplate.execute(redisScript, keys, RedisKeyUtil.getTopicId(topic, arg.getId()), arg, runTimeMillis);
        logger.info("新增延时任务:Topic:{};id:{},runTimeMillis:{},DelayQueueJob={}", topic, arg.getId(), runTimeMillis, arg.toString());

    }

    @Override
    public DelayQueueJob getJob(String topicId) {
        List<String> keys = new ArrayList<>(1);
        keys.add(RedisKeyUtil.getDelayQueueTableKey());
        DefaultRedisScript<DelayQueueJob> redisScript =new DefaultRedisScript<>();
        redisScript.setResultType(DelayQueueJob.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/getJob.lua")));
        Object args =  redisTemplate.execute(redisScript, keys, topicId);
        if(args == null)return null;
        return (DelayQueueJob) args;
    }

    @Override
    public void retryJob(String topic, String id, Object content) {
        List<String> keys = Lists.newArrayList();
        keys.add(RedisKeyUtil.getDelayQueueTableKey());
        keys.add(RedisKeyUtil.getTopicListKey(topic));
        DefaultRedisScript redisScript =new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/retryJob.lua")));
        redisTemplate.execute(redisScript,keys, RedisKeyUtil.getTopicId(topic, id), content);
    }

    @Override
    public void deleteJob(String topic, String id) {
        List<String> keys = Lists.newArrayList();
        keys.add(RedisKeyUtil.getDelayQueueTableKey());
        keys.add(RedisKeyUtil.getBucketKey());
        DefaultRedisScript redisScript =new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/deleteJob.lua")));
        redisTemplate.execute(redisScript, keys, RedisKeyUtil.getTopicId(topic, id));
    }

    @Override
    public long moveAndRtTopScore() {
//        long before = Clock.systemDefaultZone().millis();

        List<String> keys = new ArrayList<>(2);
        //移动到的待消费列表key  这里是前缀: 在lua脚本会解析真正的topic
        keys.add(RedisKeyUtil.getTopicListPreKey());
        //被移动的zset
        keys.add(RedisKeyUtil.getBucketKey());
        DefaultRedisScript<String> redisScript =new DefaultRedisScript<>();
        redisScript.setResultType(String.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/moveAndRtTopScore.lua")));
        String newTime = (String) redisTemplate.execute(redisScript,redisTemplate.getValueSerializer(),
                redisTemplate.getStringSerializer(),keys, Clock.systemDefaultZone().millis());
        //logger.info("执行一次移动操作用时:{} ",Clock.systemDefaultZone().millis()-before);
        if(ObjectUtils.isEmpty(newTime))return Long.MAX_VALUE;
        return Long.parseLong(newTime);
    }


    @Override
    public List<String> lrangeAndLTrim(String topic, int maxGet) {
        //lua 是以0开始为
        maxGet = maxGet-1;
        List<String> keys = new ArrayList<>(1);
        //移动到的待消费列表key
        keys.add(RedisKeyUtil.getTopicListKey(topic));
        DefaultRedisScript<Object> redisScript =new DefaultRedisScript<>();
        redisScript.setResultType(Object.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/lrangAndLTrim.lua")));
        Object values ;
        try {
             values = redisTemplate.execute(redisScript, redisTemplate.getValueSerializer(), redisTemplate.getStringSerializer(), keys, maxGet);
        }catch (RedisSystemException e){
            //redistemplate 有个bug  没有获取到数据的时候报空指针
            if(e.getCause() instanceof NullPointerException){
                return null;
            }else {
                logger.error("lrangeAndLTrim 操作异常;{}", ExceptionUtil.getStackTrace(e));
                throw e;
            }
        }
        List<String> list = null;
        if(values instanceof String){
            list = new ArrayList<>();
            list.add((String)values);
            return list;
        }else if(values instanceof List) {
            list = (List<String>)values;
        }
//        logger.info("========================list:{}", list);
        return list;
    }
}
