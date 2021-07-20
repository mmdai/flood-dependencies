package cn.flood.core.uid;

import cn.flood.core.uid.bean.UidSnowflake;
import cn.flood.core.uid.extend.strategy.TwitterSnowflakeStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Snowflake算法(源自twitter)
 */
//@ConditionalOnProperty(name = "spring.uid.strategy", havingValue = "snowflake", matchIfMissing = true)
public class SnowflakeUidConfiguration {


    @Bean
    public UidSnowflake getUidSnowflake(){
        return new UidSnowflake(new TwitterSnowflakeStrategy());
    }
}
