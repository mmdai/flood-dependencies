package cn.flood.db.redis.config.lettuce;

import cn.flood.db.redis.builder.RedisTemplaterFactoryBuild;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * lettuce提供了校验连接的方法，lettuce提供了校验连接的方法 只是默认没开启
 * 开启的话是每次获取连接都会校验，开启获取连接的校验。
 */
@AutoConfiguration
public class LettuceConnectionValidConfig implements InitializingBean {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisTemplaterFactoryBuild redisTemplaterFactoryBuild;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(redisConnectionFactory instanceof LettuceConnectionFactory){
            LettuceConnectionFactory c=(LettuceConnectionFactory)redisConnectionFactory;
            c.setValidateConnection(true);
        }
        Map<String, RedisTemplate<String, Object>> redisConnectionFactoryMap = redisTemplaterFactoryBuild.getRedisTemplateMap();
        if(!CollectionUtils.isEmpty(redisConnectionFactoryMap)){
            for(RedisTemplate<String, Object> redisTemplate: redisConnectionFactoryMap.values()){
                if(redisTemplate.getConnectionFactory() instanceof LettuceConnectionFactory){
                    LettuceConnectionFactory c=(LettuceConnectionFactory)redisConnectionFactory;
                    c.setValidateConnection(true);
                }
            }
        }
    }
}
