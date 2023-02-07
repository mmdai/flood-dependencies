package cn.flood.delay.redis;


import cn.flood.delay.redis.core.Callback;

/**
 * MessageListener
 *
 * @author mmdai
 * @date 2019/11/22
 */
public interface MessageListener<T> extends Callback<T> {

	String topic();

}
