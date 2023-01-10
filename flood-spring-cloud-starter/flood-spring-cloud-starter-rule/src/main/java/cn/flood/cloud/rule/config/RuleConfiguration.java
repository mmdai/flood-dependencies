package cn.flood.cloud.rule.config;

import cn.flood.cloud.rule.service.RuleCacheService;
import cn.flood.cloud.rule.service.impl.RuleCacheServiceImpl;
import cn.flood.db.redis.config.lettuce.LettuceConnectionConfiguration;
import cn.flood.db.redis.service.RedisService;
import cn.flood.db.redis.util.ApplicationContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * 规则配置
 * @author pangu
 */
@AutoConfiguration
@Import({
        RedisService.class
})
public class RuleConfiguration {

    @Bean
    public RuleCacheService ruleCacheService(RedisService redisService) {
        return new RuleCacheServiceImpl(redisService);
    }
}
