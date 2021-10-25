/**  
* <p>Title: CacheRedisConfig.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月6日  
* @version 1.0  
*/  
package cn.flood.cache.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import cn.flood.cache.CacheRedisCaffeineProperties;

/**  
* <p>Title: CacheRedisConfig</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月6日  
*/
@Configuration
@ConditionalOnProperty(name = "cache.use2L", havingValue = "false", matchIfMissing = true)
@EnableConfigurationProperties(CacheRedisCaffeineProperties.class)
public class CacheRedisConfig{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
    private CacheRedisCaffeineProperties cacheRedisCaffeineProperties;
	
	// 缓存管理器
	@Primary
	@Bean
	public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
		//默认缓存有效时间
		RedisCacheConfiguration defaultsConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ZERO)// 设置缓存永久
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())); 
		 //设置各个cache的缓存过期时间
        Map<String, Long> expires = new HashMap<>(cacheRedisCaffeineProperties.getRedis().getExpires());
        Map<String, RedisCacheConfiguration> expiresConfiguration = new HashMap<>();
        expires.forEach((k, v) -> {
        	RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(v))// 设置缓存分
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));
        	expiresConfiguration.put(k, configuration);
        });
        RedisCacheManager cacheManager =  RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory))
                .initialCacheNames(cacheRedisCaffeineProperties.getCacheNames())
                .withInitialCacheConfigurations(expiresConfiguration)
                .cacheDefaults(defaultsConfiguration)
                .build();
		 return cacheManager;
	}
	
	private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
	
}
