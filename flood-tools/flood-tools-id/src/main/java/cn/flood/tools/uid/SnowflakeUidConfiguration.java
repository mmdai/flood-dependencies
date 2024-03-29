package cn.flood.tools.uid;

import cn.flood.tools.uid.bean.UidSnowflake;
import cn.flood.tools.uid.extend.strategy.TwitterSnowflakeStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Snowflake算法(源自twitter)
 */
//@ConditionalOnProperty(name = "spring.uid.strategy", havingValue = "snowflake", matchIfMissing = true)
@AutoConfiguration
public class SnowflakeUidConfiguration {


  @Bean
  public UidSnowflake getUidSnowflake() {
    return new UidSnowflake(new TwitterSnowflakeStrategy());
  }
}
