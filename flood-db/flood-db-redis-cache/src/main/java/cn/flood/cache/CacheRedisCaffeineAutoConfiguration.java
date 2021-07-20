package cn.flood.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import cn.flood.cache.support.CacheMessageListener;
import cn.flood.cache.support.RedisCaffeineCacheManager;

/**
 * 
* <p>Title: CacheRedisCaffeineAutoConfiguration</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月6日
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(CacheRedisCaffeineProperties.class)
public class CacheRedisCaffeineAutoConfiguration {
	
	@Autowired
	private CacheRedisCaffeineProperties cacheRedisCaffeineProperties;
	
	@Bean
	@ConditionalOnBean(RedisTemplate.class)
	public RedisCaffeineCacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
		return new RedisCaffeineCacheManager(cacheRedisCaffeineProperties, redisTemplate);
	}

	
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisTemplate<String, Object> redisTemplate, 
			RedisCaffeineCacheManager redisCaffeineCacheManager) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisTemplate.getConnectionFactory());
		CacheMessageListener cacheMessageListener = new CacheMessageListener(redisTemplate, redisCaffeineCacheManager);
		redisMessageListenerContainer.addMessageListener(cacheMessageListener, new ChannelTopic(cacheRedisCaffeineProperties.getRedis().getTopic()));
		return redisMessageListenerContainer;
	}
}
