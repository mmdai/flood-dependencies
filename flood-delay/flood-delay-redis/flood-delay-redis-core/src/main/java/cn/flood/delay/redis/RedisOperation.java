package cn.flood.delay.redis;

import cn.flood.delay.entity.DelayQueueJob;
import org.springframework.data.redis.core.types.RedisClientInfo;

import java.util.List;

/**
 * @Description redis操作
 * @Author daimi
 * @Date 2019/7/30 5:38 PM
 **/
public interface RedisOperation {

    /**
     * 新增一个Job
     * @param topic
     * @param runTimeMillis  执行时间戳
     */
    void addJob(String topic, DelayQueueJob arg, long runTimeMillis);


    /**
     * 新增一个重试任务Job
     * @param topic
     * @param id
     * @param content
     */
    void retryJob(String topic, String id, Object content);

    /**
     * 删除一个Job
     * @param topic
     * @param id
     */
    void deleteJob(String topic, String id);

    /**
     * 搬运操作
     * 从待搬运zset中搬运元素到 待消费队列list中
     * @return  搬运完成之后,再返回zset中的队首元素的时间戳;
     *          如果zset没有元素了则返回  Long.MAXVALUE;
     */
    long moveAndRtTopScore();


    /**
     * 阻塞获取 待消费队列TOPIC_List中的元素;
     * 阻塞有超时时间; 如果超时则会返回Null
     * @param topic
     * @return
     */
    Object BLPOP(String topic);

    /**
     * 阻塞获取元素
     * 1之内没有获取到数据则断开连接
     * @param topic
     * @return
     */
    String BLPOPKey(String topic);

    String BLPOP(String key, long timeout);

    /**
     * 获取最多maxGet元素 ;并且将这些元素删除
     * @param topic
     * @param maxGet
     * @return
     */
    List<String> lrangeAndLTrim(String topic,int maxGet);

    /**
     * 通过topicId获取Job内容
     * @param topicId
     * @return
     */
    DelayQueueJob getJob(String topicId);

    /**
     * 将元素push到队尾
     * @param topicId
     */
    void rPush(String topicId);


    /**
     * 获取所有redis的客户端
     * @return
     */
    List<RedisClientInfo> getThisMachineAllBlpopClientList();


    /**
     * 杀掉指定客户端
     */
    void killClient(List<String> clients);
}
