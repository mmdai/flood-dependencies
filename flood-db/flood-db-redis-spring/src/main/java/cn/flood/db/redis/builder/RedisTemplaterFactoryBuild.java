package cn.flood.db.redis.builder;

import cn.flood.db.redis.exception.RedisException;
import java.util.Map;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisTemplaterWrapper工厂类 当配置了多数据源时，用户可以通过该工厂获取自己想要的数据源
 */
public class RedisTemplaterFactoryBuild {

  /**
   * 默认RedisTemplate
   */
  private RedisTemplate<String, Object> defaultRedisTemplate;
  /**
   * 多数据源RedisTemplate
   */
  private Map<String, RedisTemplate<String, Object>> redisTemplateMap;

  public RedisTemplate<String, Object> getDefaultRedisTemplate() {
    return defaultRedisTemplate;
  }

  public void setDefaultRedisTemplate(RedisTemplate<String, Object> defaultRedisTemplate) {
    this.defaultRedisTemplate = defaultRedisTemplate;
  }

  public Map<String, RedisTemplate<String, Object>> getRedisTemplateMap() {
    return redisTemplateMap;
  }

  public void setRedisTemplateMap(Map<String, RedisTemplate<String, Object>> redisTemplateMap) {
    this.redisTemplateMap = redisTemplateMap;
  }

  /**
   * 根据数据源名称获取相对应的RedisTemplate数据源
   *
   * @param dsName
   * @return
   */
  public RedisTemplate<String, Object> getRedisTemplaterByName(String dsName) {
    RedisTemplate<String, Object> redisTemplate = redisTemplateMap.get(dsName);
    if (redisTemplate == null) {
      throw new RedisException("没有相应的数据源");
    }
    return redisTemplate;
  }
}
