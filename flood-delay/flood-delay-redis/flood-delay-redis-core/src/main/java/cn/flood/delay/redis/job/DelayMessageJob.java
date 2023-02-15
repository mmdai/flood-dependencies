package cn.flood.delay.redis.job;

import cn.flood.delay.redis.configuration.Config;
import cn.flood.delay.redis.core.Callback;
import cn.flood.delay.redis.core.DQRedis;
import cn.flood.delay.redis.core.RawMessage;
import cn.flood.delay.redis.enums.ConsumeStatus;
import cn.flood.delay.redis.utils.ClassUtil;
import cn.flood.delay.redis.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * DelayMessageJob
 *
 * @author mmdai
 * @date 2019/11/21
 */
@Slf4j
public class DelayMessageJob extends BaseJob implements Runnable {

	private ExecutorService threadPool;

	private Map<String, Callback> callbacks;

	public DelayMessageJob(Config config, DQRedis dqRedis, ExecutorService threadPool) {
		super(config, dqRedis);
		this.threadPool = threadPool;
		this.callbacks = config.getCallbacks();
	}

	@Override
	public void run() {
		if (callbacks.isEmpty()) {
			return;
		}

		long now   = Instant.now().getEpochSecond();
		long begin = now - config.getTaskTtl();
		long end   = now - config.getCallbackTtl();

		try {
			List<String> keys = zrangebyscore(config.getDelayKey(), begin, end);
			if (null == keys || keys.isEmpty()) {
				return;
			}

			keys.stream()
					.filter(config::waitProcessing)
					.map(key -> (Runnable) () -> handleCallback(key))
					.forEach(threadPool::submit);
		} catch (Exception e) {
			log.error("zrangebyscore({}, {}-{})", config.getDelayKey(), begin, now, e);
		}
	}

	private <T extends Serializable> void handleCallback(final String key) {
		config.addProcessed(key);
		RawMessage rawMessage = getTask(key);
		if (null == rawMessage) {
			return;
		}
		if (!callbacks.containsKey(rawMessage.getTopic())) {
			return;
		}
		long score = Instant.now().getEpochSecond();

		// will delay message dump to wait for an ack confirmed
		// that only allows a consumer operating at this time
		if (!this.transferMessage(key, config.getDelayKey(), config.getAckKey(), score)) {
			config.processed(key);
			return;
		}

		Callback callback = callbacks.get(rawMessage.getTopic());

		Class<T> type = ClassUtil.getGenericType(callback);

		T payload;
		if (ClassUtil.isBasicType(type)) {
			payload = (T) ClassUtil.convert(type, rawMessage.getMsg());
		} else {
			payload = GsonUtil.fromJson(rawMessage.getMsg(), type);
		}

		// when the confirmed result to retry, configure a retry sending the limited time
		if (ConsumeStatus.RETRY.equals(callback.execute(payload))) {
			this.retry(key, rawMessage);
		} else {
			// consumption is successful, delete the message
			this.deleteMessage(key);
		}
		config.processed(key);
	}

	private void retry(String key, RawMessage rawMessage) {
		rawMessage.addHasRetries();
		if (rawMessage.getHasRetries() > rawMessage.getMaxRetries()) {
			this.deleteMessage(key);
			return;
		}
		redis.hset(config.getHashKey(), key, GsonUtil.toJson(rawMessage));

		// (1,2,4,8...) * X
		long now   = Instant.now().getEpochSecond();
		int  adder = (int) Math.pow(2, rawMessage.getHasRetries() - 1) * config.getRetryInterval();
		long score = now + adder;
		transferMessage(key, config.getAckKey(), config.getErrorKey(), score);
	}

}
