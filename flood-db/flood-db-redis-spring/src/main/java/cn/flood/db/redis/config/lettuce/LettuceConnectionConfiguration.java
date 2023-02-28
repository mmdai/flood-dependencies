package cn.flood.db.redis.config.lettuce;

import cn.flood.db.redis.builder.RedisConnectionFactoryBuilder;
import cn.flood.db.redis.config.RedisConnectionConfiguration;
import cn.flood.db.redis.config.properties.DynamicRedisProperties;
import io.lettuce.core.RedisClient;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

/**
 * lettuce连接配置 重写org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration
 *
 * @author daimm
 * @date 2019/5/7
 * @since 1.8
 */
@AutoConfiguration
@ConditionalOnClass({GenericObjectPool.class, RedisClient.class})
@AutoConfigureAfter({RedisProperties.class})
@EnableConfigurationProperties({RedisProperties.class, DynamicRedisProperties.class})
public class LettuceConnectionConfiguration extends RedisConnectionConfiguration {

  public LettuceConnectionConfiguration(
      RedisProperties properties,
      ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
      ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider
  ) {
    super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
  }

  @Bean(destroyMethod = "shutdown")
  @ConditionalOnMissingBean({ClientResources.class})
  public DefaultClientResources lettuceClientResources() {
    return DefaultClientResources.create();
  }

  @Bean
  @Scope("prototype")
  @ConditionalOnMissingBean({RedisConnectionFactory.class})
  public RedisConnectionFactory redisConnectionFactory(
      ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
      ClientResources clientResources, DynamicRedisProperties dynamicRedisProperties
  ) {
    LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(
        builderCustomizers,
        clientResources,
        this.getProperties().getLettuce().getPool()
    );
    return createLettuceConnectionFactory(clientConfig, dynamicRedisProperties);
  }

  @Primary
  @Bean
  @ConditionalOnMissingBean({LettuceConnectionFactory.class})
  public LettuceConnectionFactory redisLettuceConnectionFactory(
      ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
      ClientResources clientResources, DynamicRedisProperties dynamicRedisProperties
  ) {
    LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(
        builderCustomizers,
        clientResources,
        this.getProperties().getLettuce().getPool()
    );
    return createLettuceConnectionFactory(clientConfig, dynamicRedisProperties);
  }

  private LettuceConnectionFactory createLettuceConnectionFactory(
      LettuceClientConfiguration clientConfiguration,
      DynamicRedisProperties dynamicRedisProperties) {
    if (getSentinelConfig() != null) {
      return new LettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
    }
    if (getClusterConfiguration() != null) {
      return new LettuceConnectionFactory(getClusterConfiguration(), clientConfiguration);
    }
    if (getStandaloneConfig() != null) {
      return new LettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }
    // 获取多个Redis配置参数
    Map<String, RedisProperties> connection = dynamicRedisProperties.getConnection();
    AtomicReference<LettuceConnectionFactory> dynamic = new AtomicReference<>();
    connection.forEach((key, value) -> {
      //如果配置了默认的数据源，则使用默认的数据源
      String defaultDataSource = dynamicRedisProperties.getDefaultDataSource();
      if (defaultDataSource.equals(key)) {
        dynamic.set(dynamicBuild(value));
      }
    });
    return dynamic.get();
  }

  /**
   * 动态构建ConnectionFactory
   *
   * @param properties 配置属性
   * @return
   */
  private LettuceConnectionFactory dynamicBuild(RedisProperties properties) {
    RedisStandaloneConfiguration redisStandaloneConfiguration = RedisConnectionFactoryBuilder
        .getRedisStandaloneConfiguration(properties);
    RedisClusterConfiguration redisClusterConfiguration = RedisConnectionFactoryBuilder
        .getRedisClusterConfiguration(properties);
    RedisSentinelConfiguration redisSentinelConfiguration = RedisConnectionFactoryBuilder
        .getRedisSentinelConfiguration(properties);
    LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder
        builder = LettucePoolingClientConfiguration.builder();
    if (properties.getTimeout() != null) {
      // 设置超时时间
      Duration timeout = properties.getTimeout();
      builder.commandTimeout(timeout).shutdownTimeout(timeout);
    }
    // 连接池构造器
    builder.poolConfig(RedisConnectionFactoryBuilder.poolConfig(properties));
    //通过构造器来构造lettuce客户端配置
    LettucePoolingClientConfiguration lettuceClientConfiguration = builder.build();

    if (properties.getCluster() != null) {
      LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
          redisClusterConfiguration, lettuceClientConfiguration);
      lettuceConnectionFactory.afterPropertiesSet();
      return lettuceConnectionFactory;
    }
    if (properties.getSentinel() != null) {
      LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
          redisSentinelConfiguration, lettuceClientConfiguration);
      lettuceConnectionFactory.afterPropertiesSet();
      return lettuceConnectionFactory;
    }
    //单机配置 + 客户端配置 = lettuce连接工厂
    LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
        redisStandaloneConfiguration, lettuceClientConfiguration);
    lettuceConnectionFactory.afterPropertiesSet();
    return lettuceConnectionFactory;  //集群配置 + 客户端配置 = lettuce连接工厂

  }

  private LettuceClientConfiguration getLettuceClientConfiguration(
      ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
      ClientResources clientResources, RedisProperties.Pool pool) {
    LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(pool);
    applyProperties(builder);
    if (StringUtils.hasText(getProperties().getUrl())) {
      customizeConfigurationFromUrl(builder);
    }
    builder.clientResources(clientResources);
    builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return builder.build();
  }

  private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(
      RedisProperties.Pool pool) {
    if (pool == null) {
      return LettuceClientConfiguration.builder();
    }
    return LettucePoolingClientConfiguration.builder().poolConfig(
        this.getPoolConfig(pool, new GenericObjectPoolConfig<>())
    );
  }

  private LettuceClientConfiguration.LettuceClientConfigurationBuilder applyProperties(
      LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
    if (getProperties().isSsl()) {
      builder.useSsl();
    }
    if (getProperties().getTimeout() != null) {
      builder.commandTimeout(getProperties().getTimeout());
    }
    if (getProperties().getLettuce() != null) {
      RedisProperties.Lettuce lettuce = getProperties().getLettuce();
      if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
        builder.shutdownTimeout(getProperties().getLettuce().getShutdownTimeout());
      }
    }
    return builder;
  }

  private void customizeConfigurationFromUrl(
      LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
    ConnectionInfo connectionInfo = parseUrl(getProperties().getUrl());
    if (connectionInfo.isUseSsl()) {
      builder.useSsl();
    }
  }
}
