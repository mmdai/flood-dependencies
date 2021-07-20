package cn.flood.delay.common;

/**
 * @Description 延迟队列全局参数
 * @Author daimi
 * @Date 2019/7/30 4:48 PM
 **/
public class RedisQueueKeys {

    /**
     * 是一个Hash_Table结构；里面存储了所有的延迟队列的信息;KV结构；
     * K=TOPIC:ID    V=CONENT;  V由客户端传入的数据,消费的时候回传；
     */
    public static final String  REDIS_DELAY_TABLE = "Redis_Delay_Table";


    /**
     * 延迟队列的有序集合; 存放K=TOPIC:ID 和需要的执行时间戳;
     * 根据时间戳排序;
     */
    public static final String  RD_ZSET_BUCKET_PRE = "RD_ZSET_BUCKET:";


    /**
     * list结构; 每个Topic一个list；list存放的都是当前需要被消费的Job;
     */
    public static final String  RD_LIST_TOPIC_PRE = "RD_LIST_TOPIC:";



}
