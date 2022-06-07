package cn.flood.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class StringContextConfiguration {

    @Bean
    public SpringBeanManager getSpringContextManager(@Autowired ApplicationContext applicationContext){
        return new SpringBeanManager(applicationContext);
    }
}
