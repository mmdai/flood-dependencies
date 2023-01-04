package cn.flood.db.redis.config.properties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 将yml中的spring.dynamic.redis下的配置注入到类中的属性中
 */
@ConfigurationProperties(prefix = DynamicRedisProperties.PREFIX)
public class DynamicRedisProperties {

    public static final String PREFIX = "spring.redis.dynamic";

    /*多数据源指定默认数据源*/
    private String defaultDataSource;
    /*多数据源配置属性*/
    private Map<String, RedisProperties> connection = new HashMap<>();

    public static String getPREFIX() {
        return PREFIX;
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
