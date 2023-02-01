package cn.flood.cloud.rule.config;

import cn.flood.cloud.rule.service.RuleCacheService;
import cn.flood.cloud.rule.service.impl.RuleCacheServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 规则配置
 * @author pangu
 */
@AutoConfiguration
public class RuleConfiguration {

    @Bean
    public RuleCacheService ruleCacheService() {
        return new RuleCacheServiceImpl();
    }
}
