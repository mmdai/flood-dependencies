/**  
* <p>Title: RedisConfig.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月5日  
* @version 1.0  
*/  
package cn.flood.redis.config;

import java.lang.reflect.Method;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import cn.flood.redis.service.RedisService;
import cn.flood.redis.service.impl.RedisServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**  
* <p>Title: RedisConfig</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月5日  
*/
@Configuration
@EnableCaching // 开启缓存支持
@Slf4j
public class RedisConfig {
	
	/**
	 * 
	 * <p>Title: keyGenerator</p>  
	 * <p>Description: 自定义的缓存key的生成策略 若想使用这个key  只需要讲注解上keyGenerator的值设置为keyGenerator即可</p>  
	 * @return 自定义策略生成的key
	 */
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuffer sb = new StringBuffer();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}

//	// 缓存管理器
//	@Primary
//	@Bean
//	public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
//		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(30))
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
//                .disableCachingNullValues();
//
//        RedisCacheManager redisCacheManager = RedisCacheManager.builder(lettuceConnectionFactory)
//                .cacheDefaults(config)
//                .transactionAware()
//                .build();
//        log.info("------> 自定义RedisCacheManager加载完成");
//        return redisCacheManager;
//	}

	@Primary
	@Bean("redisTemplate")
	public RedisTemplate<String, Object> getSingleRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
		log.info("------> redisTemplate 加载完成");
		// 设置序列化
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
//	    om.enableDefaultTyping(DefaultTyping.NON_FINAL);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
				DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		// 配置redisTemplate
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory);
		RedisSerializer<?> stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);// key序列化
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);// value序列化
		redisTemplate.setHashKeySerializer(stringSerializer);// Hash key序列化
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);// Hash value序列化
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public RedisService getRedisService(RedisTemplate<String, Object> redisTemplate) {
		return new RedisServiceImpl(redisTemplate);
	}
	
	private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
	
}
