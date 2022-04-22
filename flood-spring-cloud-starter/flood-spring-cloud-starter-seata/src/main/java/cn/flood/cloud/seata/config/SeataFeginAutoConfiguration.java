package cn.flood.cloud.seata.config;

import cn.flood.cloud.config.FloodCloudAutoConfiguration;
import cn.flood.cloud.seata.rest.SeataRestTemplateInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/20 13:04
 */
@AutoConfigureBefore({FloodCloudAutoConfiguration.class})
public class SeataFeginAutoConfiguration {

//    @Primary
//    @Bean
//    public DataSourceProxy dataSource(DruidDataSource druidDataSource) {
//        return new DataSourceProxy(druidDataSource);
//    }


    @Bean
    public SeataRestTemplateInterceptor seataRestTemplateInterceptor(){
        return new SeataRestTemplateInterceptor();
    }


}
