package cn.flood.cloud.rule.config;

import cn.flood.cloud.rule.service.RuleCacheService;
import cn.flood.cloud.rule.service.impl.RuleCacheServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 规则配置
 * @author pangu
 */
@Configuration
public class RuleConfiguration {

    @Bean
    public RuleCacheService ruleCacheService() {
        return new RuleCacheServiceImpl();
    }
}
