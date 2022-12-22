package cn.flood.db.redis.config.cache;

import cn.flood.db.redis.config.lettuce.LettuceConnectionConfiguration;
import cn.flood.db.redis.util.ApplicationContextUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import cn.flood.db.redis.config.JsonRedisSerializer;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * redis缓存配置
 * 重写org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
 * @author daimm
 * @date 2019/10/4
 * @since 1.8
 */
@AutoConfiguration
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnMissingBean(CacheManager.class)
@EnableConfigurationProperties({CacheProperties.class})
@Import({ LettuceConnectionConfiguration.class})
public class RedisCacheConfiguration {

    @Bean
    RedisCacheManager cacheManager(
            CacheProperties cacheProperties,
            ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
            ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
            RedisConnectionFactory redisConnectionFactory
    ) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(
                determineConfiguration(cacheProperties, redisCacheConfiguration));
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return new CacheManagerCustomizers(null).customize(builder.build());
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
            CacheProperties cacheProperties,
            ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration
    ) {
        return redisCacheConfiguration.getIfAvailable(() -> createConfiguration(cacheProperties));
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration createConfiguration(
            CacheProperties cacheProperties) {
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
                .defaultCacheConfig();
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new JsonRedisSerializer())
        );
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

}
