package cn.flood.cloud.seata.config;

import cn.flood.cloud.seata.idempotent.JdbcResultHolder;
import cn.flood.cloud.seata.idempotent.RedisResultHolder;
import cn.flood.cloud.seata.idempotent.ResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/5/6 18:06
 */
@AutoConfiguration
public class SeataResultHolderConfiguration implements ApplicationContextAware {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 注入redisTokenStore
     */
    @ConditionalOnProperty(name = "seata.store-type", havingValue = "0")
    @Bean
    public ResultHolder redisResultHolder() {
        RedisTemplate redisTemplate = getBean(RedisTemplate.class);
        if (redisTemplate == null) {
            logger.error("ResultHolder: RedisTemplate is null");
        }
        return new RedisResultHolder(redisTemplate);
    }

    /**
     * 注入jdbcTokenStore
     */
    @ConditionalOnProperty(name = "seata.store-type", havingValue = "1")
    @Bean
    public ResultHolder jdbcResultHolder() {
        DataSource dataSource = getBean(DataSource.class);
        if (dataSource == null) {
            logger.error("ResultHolder: DataSource is null");
        }
        return new JdbcResultHolder(dataSource);
    }


    /**
     * 获取Bean
     */
    private <T> T getBean(Class<T> clazz) {
        T bean = null;
        Collection<T> beans = applicationContext.getBeansOfType(clazz).values();
        while (beans.iterator().hasNext()) {
            bean = beans.iterator().next();
            if (bean != null) {
                break;
            }
        }
        return bean;
    }
}
