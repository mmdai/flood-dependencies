package cn.flood.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StringContextConfiguration {

    @Bean
    public SpringContextManager getSpringContextManager(@Autowired ApplicationContext applicationContext){
        return new SpringContextManager(applicationContext);
    }
}
