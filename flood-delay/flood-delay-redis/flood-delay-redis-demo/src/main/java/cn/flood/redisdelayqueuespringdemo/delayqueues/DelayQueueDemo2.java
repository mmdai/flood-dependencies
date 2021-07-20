package cn.flood.redisdelayqueuespringdemo.delayqueues;

import cn.flood.delay.entity.DelayQueueJob;
import cn.flood.delay.service.RedisDelayQueue;
import cn.flood.delay.service.impl.AbstractTopicRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * @Description 可以把 延迟任务的 操作都放在这个类里面;方便一点
 * @Author daimm
 * @Date 2019/7/31 12:22 PM
 **/
@Service
public class DelayQueueDemo2 extends AbstractTopicRegister<DelayQueueJob> {
    private static final Logger logger = LoggerFactory.getLogger(DelayQueueDemo2.class);


    @Autowired
    RedisDelayQueue redisDelayQueue;


    @Override
    public String getTopic() {
        return TopicEnums.DEMO_TOPIC_2.getTopic();
    }


    @Override
    public void execute(DelayQueueJob args) {
        //故意抛出一个异常
       /* if(1/0==0){

        }*/

        logger.info("执行了Demo2的延时任务..{}",args);
    }

    @Override
    public void retryOutTimes(DelayQueueJob args) {
        super.retryOutTimes(args);
        // you can do something ;like send a message to the developer
        logger.error("假装给开发者发了一条告警短信..");
    }

    /**
     * 异步新增一个Demo2的延时任务
     * @param userId
     * @param delayTimes
     */
    public void addDemo2DelayQueue(String userId,long delayTimes){
        // do other something
        redisDelayQueue.add(new DelayQueueJob(userId),getTopic(), Clock.systemDefaultZone().millis()+delayTimes);
    }

    /**
     * 异步删除一个延时任务
     * @param userId
     */
    public void delDemo2Queue(String userId){
        // do other something
        redisDelayQueue.delete(getTopic(),userId);
    }
}
