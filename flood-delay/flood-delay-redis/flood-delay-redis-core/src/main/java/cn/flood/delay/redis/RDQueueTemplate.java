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

	public <T extends Serializable> void syncPush(Message<T> message) throws RDQException {
		rdQueue.syncPush(message);
	}

	public <T extends Serializable> void asyncPush(Message<T> message, BiConsumer<String, ? super Throwable> action) throws RDQException {
		rdQueue.asyncPush(message, action);
	}

}
