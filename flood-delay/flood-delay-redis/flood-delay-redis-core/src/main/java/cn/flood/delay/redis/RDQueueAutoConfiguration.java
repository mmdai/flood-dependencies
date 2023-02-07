package cn.flood.delay.redis;

import cn.flood.delay.redis.configuration.Config;
import cn.flood.delay.redis.core.RDQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * RDQueueAutoConfiguration
 *
 * @author mmdai
 * @date 2019/11/21
 */
@AutoConfiguration
@EnableConfigurationProperties(Config.class)
public class RDQueueAutoConfiguration {

	@Autowired
	private Config config;


	@Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean
	public RDQueue rdQueue() {
		return new RDQueue(config);
	}

	@Bean
	@ConditionalOnMissingBean
	public RDQueueTemplate rdQueueTemplate(@Autowired RDQueue rdQueue) {
		return new RDQueueTemplate(rdQueue);
	}

	@Bean
	public MessageListenerContainer messageListenerContainer(@Autowired Config config) {
		return new MessageListenerContainer(config);
	}

}
