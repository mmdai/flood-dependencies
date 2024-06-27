package cn.flood.cloud.rule.config;

import cn.flood.cloud.rule.service.RuleCacheService;
import cn.flood.cloud.rule.service.impl.RuleCacheServiceImpl;
import cn.flood.db.redis.RedisAutoConfiguration;
import cn.flood.db.redis.service.RedisService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * 规则配置
 *
 * @author pangu
 */
@AutoConfiguration
@AutoConfigureBefore(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@Import({
        RedisAutoConfiguration.class
})
public class RuleConfiguration {

  @Bean
  public RuleCacheService ruleCacheService(RedisService redisService) {
    return new RuleCacheServiceImpl(redisService);
  }
}
