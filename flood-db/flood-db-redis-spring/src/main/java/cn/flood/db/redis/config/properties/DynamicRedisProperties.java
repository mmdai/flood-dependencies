package cn.flood.db.redis.config.properties;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 将yml中的spring.dynamic.redis下的配置注入到类中的属性中
 */
@ConfigurationProperties(prefix = DynamicRedisProperties.PREFIX)
public class DynamicRedisProperties {

  public static final String PREFIX = "spring.redis.dynamic";

  /*是否开启多数据源*/
  private boolean enabled;
  /*多数据源指定默认数据源*/
  private String defaultDataSource;
  /*多数据源配置属性*/
  private Map<String, RedisProperties> connection = new HashMap<>();

  public static String getPREFIX() {
    return PREFIX;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getDefaultDataSource() {
    return defaultDataSource;
  }

  public void setDefaultDataSource(String defaultDataSource) {
    this.defaultDataSource = defaultDataSource;
  }

  public Map<String, RedisProperties> getConnection() {
    return connection;
  }

  public void setConnection(Map<String, RedisProperties> connection) {
    this.connection = connection;
  }
}
