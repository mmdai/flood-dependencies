package cn.flood.delay.utils;

import cn.flood.delay.core.RedisDelayQueueContext;
import cn.flood.delay.common.RedisQueueKeys;

/**
 * @Description
 * @Author daimi
 * @Date 2019/7/31 10:18 AM
 **/
public class RedisKeyUtil {

    /**
     * 获取RD_LIST_TOPIC 的Key前缀
     * @return
     */
    public static String getTopicListPreKey(){
        return getProjectName().concat(":").concat(RedisQueueKeys.RD_LIST_TOPIC_PRE);
    }

    /**
     * 获取RD_LIST_TOPIC某个TOPIC的 Key
     * @param topic
     * @return
     */
    public static String getTopicListKey(String topic){
        return getTopicListPreKey().concat(topic);
    }

    /**
     * 从member中解析出TopicList的key
     * @param member
     * @return
     */
    public static String getTopicListKeyByMember(String member){
        return RedisKeyUtil.getTopicListKey(RedisKeyUtil.getTopicKeyBymember(member));
    }

    /**
     * 拼接TOPIC:ID
     * @param topic
     * @return
     */
    public static String getTopicId(String topic,String id){
        return topic.concat(":").concat(id);
    }



    /**
     * 获取所有Job数据存放的Hash_Table 的Key
     * @return
     */
    public static String getDelayQueueTableKey(){
        return getProjectName().concat(":").concat(RedisQueueKeys.REDIS_DELAY_TABLE);
    }

    /**
     * 获取延迟列表 ZSet的Key
     * @return
     */
    public static String getBucketKey(){
        return getProjectName().concat(":").concat(RedisQueueKeys.RD_ZSET_BUCKET_PRE);
    }


    /**
     * 根据member获取Topic
     * @param member
     * @return
     */
    public static String getTopicKeyBymember(String member){
        String[] s = member.split(":");
        return s[0];
    }


    /**
     * 获取项目名; 加入{}   标记hash_tag ; 将所有的可以都落在同一台机器上
     * TODO..  待验证是否放在一台机器上
     * @return
     */
    public static String getProjectName(){
        return "{".concat(RedisDelayQueueContext.PROJECTNAME).concat("}");
    }


}
