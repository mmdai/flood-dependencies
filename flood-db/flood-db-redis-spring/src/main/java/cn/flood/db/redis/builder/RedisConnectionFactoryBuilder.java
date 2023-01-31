package cn.flood.db.redis.builder;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据配置属性构建RedisConnectionFactoryBuilder
 * 根据配置文件中的配置
 * RedisConnectionFactory
 */
public class RedisConnectionFactoryBuilder {

    /**
     * 动态构建ConnectionFactory
     *
     * @param properties 配置属性
     * @return
     */
    public static RedisConnectionFactory build(RedisProperties properties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = getRedisStandaloneConfiguration(properties);
        RedisClusterConfiguration redisClusterConfiguration = getRedisClusterConfiguration(properties);
        RedisSentinelConfiguration redisSentinelConfiguration = getRedisSentinelConfiguration(properties);
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder
                builder = LettucePoolingClientConfiguration.builder();
        if (properties.getTimeout() != null) {
            // 设置超时时间
            Duration timeout = properties.getTimeout();
            builder.commandTimeout(timeout).shutdownTimeout(timeout);
        }
        // 连接池构造器
        builder.poolConfig(poolConfig(properties));
        //通过构造器来构造lettuce客户端配置
        LettucePoolingClientConfiguration lettuceClientConfiguration = builder.build();

        if (properties.getCluster() != null) {
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
            lettuceConnectionFactory.afterPropertiesSet();
            return lettuceConnectionFactory;
        }
        if(properties.getSentinel() != null){
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration, lettuceClientConfiguration);
            lettuceConnectionFactory.afterPropertiesSet();
            return lettuceConnectionFactory;
        }
        //单机配置 + 客户端配置 = lettuce连接工厂
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;  //集群配置 + 客户端配置 = lettuce连接工厂

    }

    /**
     * 单机配置信息
     *
     * @param properties
     * @return
     */
    public static RedisStandaloneConfiguration getRedisStandaloneConfiguration(RedisProperties properties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(properties.getHost());
        redisStandaloneConfiguration.setPort(properties.getPort());
        if (!ObjectUtils.isEmpty(properties.getPassword())) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        }
        if (properties.getDatabase() != 0) {
            redisStandaloneConfiguration.setDatabase(properties.getDatabase());
        }
        return redisStandaloneConfiguration;
    }

    /**
     * 哨兵配置信息
     *
     * @param properties
     * @return
     */
    public static RedisSentinelConfiguration getRedisSentinelConfiguration(RedisProperties properties) {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        RedisProperties.Sentinel sentinelProperties = properties.getSentinel();
        if (properties.getSentinel() != null) {
            redisSentinelConfiguration.master(sentinelProperties.getMaster());
            redisSentinelConfiguration.setSentinels(createSentinels(sentinelProperties));
            if (!ObjectUtils.isEmpty(properties.getPassword())) {
                redisSentinelConfiguration.setPassword(properties.getPassword());
            }
            if (properties.getDatabase() != 0) {
                redisSentinelConfiguration.setDatabase(properties.getDatabase());
            }
        }
        return redisSentinelConfiguration;
    }

    private static List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.notNull(parts, "Must be defined as 'host:port'");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
            }
            catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    /**
     * 集群配置信息
     *
     * @param properties
     * @return
     */
    public static RedisClusterConfiguration getRedisClusterConfiguration(RedisProperties properties) {
        //创建redis集群配置类
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        if (properties.getCluster() != null) {
            List<String> nodes = properties.getCluster().getNodes();
            //redis集群密码
            if (!ObjectUtils.isEmpty(properties.getPassword())) {
                redisClusterConfiguration.setPassword(properties.getPassword());
            }
            for (String node : nodes) {
                String[] split = node.split(":");
                redisClusterConfiguration.clusterNode(split[0], Integer.parseInt(split[1]));
            }
            if (properties.getCluster().getMaxRedirects() > 0) {
                redisClusterConfiguration.setMaxRedirects(properties.getCluster().getMaxRedirects());
            }
        }
        return redisClusterConfiguration;
    }


    /**
     * 动态配置连接池
     *
     * @param properties
     * @return
     */
    public static GenericObjectPoolConfig poolConfig(RedisProperties properties) {
        RedisProperties.Pool jedisPool = properties.getJedis().getPool();
        RedisProperties.Pool lettucePool = properties.getLettuce().getPool();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        if (jedisPool != null) {
            poolConfig.setMaxTotal(jedisPool.getMaxActive());
            poolConfig.setMaxIdle(jedisPool.getMaxIdle());
            poolConfig.setMinIdle(jedisPool.getMinIdle());
            if (jedisPool.getTimeBetweenEvictionRuns() != null) {
                poolConfig.setTimeBetweenEvictionRuns(jedisPool.getTimeBetweenEvictionRuns());
            }
            if (jedisPool.getMaxWait() != null) {
                poolConfig.setMaxWait(jedisPool.getMaxWait());
            }
        }
        if (lettucePool != null) {
            poolConfig.setMaxTotal(lettucePool.getMaxActive());
            poolConfig.setMaxIdle(lettucePool.getMaxIdle());
            poolConfig.setMinIdle(lettucePool.getMinIdle());
            if (lettucePool.getTimeBetweenEvictionRuns() != null) {
                poolConfig.setTimeBetweenEvictionRuns(lettucePool.getTimeBetweenEvictionRuns());
            }
            if (lettucePool.getMaxWait() != null) {
                poolConfig.setMaxWait(lettucePool.getMaxWait());
            }
        }
        return poolConfig;
    }
}