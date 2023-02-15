package cn.flood.delay.redis;

import cn.flood.delay.redis.core.Message;
import cn.flood.delay.redis.core.RDQueue;
import cn.flood.delay.redis.exception.RDQException;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * @author mmdai
 * @date 2019/11/21
 */
public class RDQueueTemplate {

	private RDQueue rdQueue;

	public RDQueueTemplate(RDQueue rdQueue) {
		this.rdQueue = rdQueue;
	}

	/**
	 * 同步推送消息到延时队列
	 * @param message
	 * @param <T>
	 * @throws RDQException
	 */
	public <T extends Serializable> void syncPush(Message<T> message) throws RDQException {
		rdQueue.syncPushDelayQueue(message);
	}

	/**
	 * 异步推送消息到延时队列
	 * @param message
	 * @param action
	 * @param <T>
	 * @throws RDQException
	 */
	public <T extends Serializable> void asyncPush(Message<T> message, BiConsumer<String, ? super Throwable> action) throws RDQException {
		rdQueue.asyncPushDelayQueue(message, action);
	}

	/**
	 * 删除延时队列里的消息
	 * @param key
	 * @throws RDQException
	 */
	public void delete(String key) throws RDQException {
		rdQueue.delDelayQueue(key);
	}

}
