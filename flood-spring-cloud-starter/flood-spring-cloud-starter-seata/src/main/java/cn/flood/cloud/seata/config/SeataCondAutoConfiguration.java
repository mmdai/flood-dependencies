package cn.flood.cloud.seata.config;

import cn.flood.cloud.config.FloodCloudAutoConfiguration;
import cn.flood.cloud.seata.rest.SeataRestTemplateInterceptor;
import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/20 13:04
 */
@AutoConfigureBefore({FloodCloudAutoConfiguration.class})
public class SeataCondAutoConfiguration {

    @Primary
    @Bean
    public DataSourceProxy dataSource(@Qualifier("dataSource") DruidDataSource druidDataSource) {
        return new DataSourceProxy(druidDataSource);
    }


    @Bean
    public SeataRestTemplateInterceptor seataRestTemplateInterceptor(){
        return new SeataRestTemplateInterceptor();
    }


}
