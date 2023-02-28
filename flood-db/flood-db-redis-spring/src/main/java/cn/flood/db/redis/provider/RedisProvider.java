package cn.flood.db.redis.provider;

import java.util.Map;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 加载出动态连接工厂接口
 *
 * @author mmdai
 * @version 1.0
 * @date 2022/11/7 10:33
 */
public interface RedisProvider {

  Map<String, RedisConnectionFactory> loadRedis();
}
