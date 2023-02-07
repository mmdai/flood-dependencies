package cn.flood.delay.redis.core;


import cn.flood.delay.redis.enums.ConsumeStatus;

/**
 * Callback
 *
 * @author mmdai
 * @date 2019/11/21
 */
public interface Callback<T> {

	ConsumeStatus execute(T data);

}
