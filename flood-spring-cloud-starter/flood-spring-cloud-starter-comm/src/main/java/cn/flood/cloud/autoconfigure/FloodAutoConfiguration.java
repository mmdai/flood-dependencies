package cn.flood.cloud.autoconfigure;

import cn.flood.cloud.service.ValidCodeService;
import cn.flood.cloud.service.impl.ValidCodeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan(basePackages = {"cn.flood.aop",
//"cn.flood.filter","cn.flood.handle"})
public class FloodAutoConfiguration {

    @Bean
    public ValidCodeService validCodeService(){
        return new ValidCodeServiceImpl();
    }

}
