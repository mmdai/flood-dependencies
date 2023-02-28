package cn.flood.cloud.log.config;


import cn.flood.cloud.log.properties.AuditLogProperties;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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
  @AutoConfiguration
  @ConditionalOnClass(DataSource.class)
  @EnableConfigurationProperties(LogDbProperties.class)
  public static class LogDbAutoConfigure {

  }
}
