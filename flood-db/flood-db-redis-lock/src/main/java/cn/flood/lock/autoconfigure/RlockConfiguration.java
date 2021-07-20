package cn.flood.lock.autoconfigure;

import cn.flood.lock.autoconfigure.config.RlockConfig;
import cn.flood.lock.autoconfigure.core.BusinessKeyProvider;
import cn.flood.lock.autoconfigure.core.RlockAspectHandler;
import cn.flood.lock.autoconfigure.core.LockInfoProvider;
import cn.flood.lock.autoconfigure.lock.LockFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by on 2017/12/29.
 * Content :适用于内部低版本spring mvc项目配置,redisson外化配置
 */
@Configuration
@Import({RlockAspectHandler.class})
public class RlockConfiguration {
    @Bean
    public LockInfoProvider lockInfoProvider(){
        return new LockInfoProvider();
    }

    @Bean
    public BusinessKeyProvider businessKeyProvider(){
        return new BusinessKeyProvider();
    }

    @Bean
    public LockFactory lockFactory(){
        return new LockFactory();
    }
    @Bean
    public RlockConfig rlockConfig(){
        return new RlockConfig();
    }
}
