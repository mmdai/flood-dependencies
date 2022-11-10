package cn.flood.db.redis.lock.autoconfigure;

import cn.flood.base.core.Func;
import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import cn.flood.db.redis.lock.autoconfigure.config.RlockConfig;
import cn.flood.db.redis.lock.autoconfigure.core.BusinessKeyProvider;
import cn.flood.db.redis.lock.autoconfigure.core.RlockAspectHandler;
import cn.flood.db.redis.lock.autoconfigure.core.LockInfoProvider;
import cn.flood.db.redis.lock.autoconfigure.lock.LockFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;

/**
 *
 * @author daimm
 * @date 2017/12/29
 * Content :klock自动装配
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = RlockConfig.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RlockConfig.class)
@Import({RlockAspectHandler.class})
public class RlockAutoConfiguration {

    @Autowired
    private RlockConfig rlockConfig;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    RedissonClient redisson() throws Exception {
        Config config = new Config();
        if(rlockConfig.getClusterServer()!=null){
            config.useClusterServers().addNodeAddress(rlockConfig.getClusterServer().getNodeAddresses());
            if(Func.isNotEmpty(rlockConfig.getPassword())){
                config.useClusterServers().setPassword(rlockConfig.getPassword());
            }
        }else {
            config.useSingleServer().setAddress(rlockConfig.getAddress())
                    .setDatabase(rlockConfig.getDatabase());
            if(Func.isNotEmpty(rlockConfig.getPassword())){
                config.useSingleServer().setPassword(rlockConfig.getPassword());
            }
            config.useSingleServer().setConnectionMinimumIdleSize(15);
            config.useSingleServer().setConnectionPoolSize(20);
        }
        Codec codec=(Codec) ClassUtils.forName(rlockConfig.getCodec(),ClassUtils.getDefaultClassLoader()).newInstance();
        config.setCodec(codec);
        config.setEventLoopGroup(new NioEventLoopGroup());
        config.setThreads(rlockConfig.getThreads());
        config.setNettyThreads(rlockConfig.getThreads());
        return Redisson.create(config);
    }

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
}
