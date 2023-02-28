package cn.flood.db.redis.provider.impl;

import cn.flood.db.redis.builder.RedisConnectionFactoryBuilder;
import cn.flood.db.redis.config.properties.DynamicRedisProperties;
import cn.flood.db.redis.provider.RedisProvider;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 多数据源连接工厂提供者
 *
 * @author mmdai
 * @version 1.0
 * @date 2022/11/7 10:31
 */
public class DynamicRedisProvider implements RedisProvider {

  public DynamicRedisProperties dynamicRedisProperties;

  public DynamicRedisProvider(DynamicRedisProperties properties) {
    this.dynamicRedisProperties = properties;
  }

  public DynamicRedisProperties getDynamicRedisProperties() {
    return dynamicRedisProperties;
  }

  public void setDynamicRedisProperties(DynamicRedisProperties dynamicRedisProperties) {
    this.dynamicRedisProperties = dynamicRedisProperties;
  }

  @Override
  public Map<String, RedisConnectionFactory> loadRedis() {
    // 获取多个Redis配置参数
    Map<String, RedisProperties> connection = this.dynamicRedisProperties.getConnection();
    Map<String, RedisConnectionFactory> connectionMap = new HashMap<>(connection.size());
    connection.forEach((key, value) -> {
      connectionMap.computeIfAbsent(key, k -> RedisConnectionFactoryBuilder.build(value));
    });

    return connectionMap;
  }
}
