package cn.flood.db.redis.config.cache;

import cn.flood.db.redis.RedisAutoConfiguration;
import cn.flood.db.redis.config.cache.support.CacheMessageListener;
import cn.flood.db.redis.config.cache.support.RedisCaffeineCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;


/**
 * 
* <p>Title: CacheRedisCaffeineAutoConfiguration</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月6日
 */
@EnableCaching // 开启缓存支持
@AutoConfiguration
@ConditionalOnProperty(name = "cache.use2L", havingValue = "false", matchIfMissing = true)
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

	//  // 缓存管理器
//  @Primary
//  @Bean
//  public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
//    //默认缓存有效时间
//    RedisCacheConfiguration defaultsConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//        .entryTtl(Duration.ZERO)// 设置缓存永久
//        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()));
//    //设置各个cache的缓存过期时间
//    Map<String, Long> expires = new HashMap<>(cacheRedisCaffeineProperties.getRedis().getExpires());
//    Map<String, RedisCacheConfiguration> expiresConfiguration = new HashMap<>();
//    expires.forEach((k, v) -> {
//      RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
//          .entryTtl(Duration.ofMinutes(v))// 设置缓存分
//          .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//          .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()));
//      expiresConfiguration.put(k, configuration);
//    });
//    RedisCacheManager cacheManager =  RedisCacheManager
//        .builder(RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory))
//        .initialCacheNames(cacheRedisCaffeineProperties.getCacheNames())
//        .withInitialCacheConfigurations(expiresConfiguration)
//        .cacheDefaults(defaultsConfiguration)
//        .build();
//    return cacheManager;
//  }
	
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
