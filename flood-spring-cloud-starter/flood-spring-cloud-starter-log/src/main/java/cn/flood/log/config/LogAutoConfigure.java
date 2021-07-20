package cn.flood.log.config;


import cn.flood.log.properties.AuditLogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 日志自动配置
 *
 * @author zlt
 * @date 2019/8/13
 */
@EnableConfigurationProperties({AuditLogProperties.class})
public class LogAutoConfigure {
    /**
     * 日志数据库配置
     */
    @Configuration
    @ConditionalOnClass(DataSource.class)
    @EnableConfigurationProperties(LogDbProperties.class)
    public static class LogDbAutoConfigure {}
}
