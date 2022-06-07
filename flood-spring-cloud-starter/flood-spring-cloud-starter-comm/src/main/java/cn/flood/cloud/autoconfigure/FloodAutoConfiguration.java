package cn.flood.cloud.autoconfigure;

import cn.flood.cloud.service.ValidCodeService;
import cn.flood.cloud.service.impl.ValidCodeServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
//@ComponentScan(basePackages = {"cn.flood.aop",
//"cn.flood.filter","cn.flood.handle"})
public class FloodAutoConfiguration {

    @Bean
    public ValidCodeService validCodeService(){
        return new ValidCodeServiceImpl();
    }

}
