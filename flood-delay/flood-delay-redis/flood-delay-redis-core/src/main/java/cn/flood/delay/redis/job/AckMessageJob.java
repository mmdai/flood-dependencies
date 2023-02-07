package cn.flood.delay.redis.job;

import cn.flood.delay.redis.configuration.Config;
import cn.flood.delay.redis.core.DQRedis;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

/**
 * AckMessageJob
 *
 * @author mmdai
 * @date 2019/11/21
 */
@Slf4j
public class AckMessageJob extends BaseJob implements Runnable {

	public AckMessageJob(Config config, DQRedis redis) {
		super(config, redis);
	}

	@Override
	public void run() {
		long now   = Instant.now().getEpochSecond();
		long begin = now - config.getTaskTtl();

		try {
			List<String> keys = zrangebyscore(config.getAckKey(), begin, now);
			if (null == keys || keys.isEmpty()) {
				return;
			}

			keys.stream()
					.filter(config::waitProcessing)
					.forEach(key ->
							transferMessage(key, config.getAckKey(), config.getDelayKey(), Instant.now().getEpochSecond())
					);
		} catch (Exception e){
			log.error("zrangebyscore({}, {}-{})", config.getAckKey(), begin, now, e);
		}

	}

}
