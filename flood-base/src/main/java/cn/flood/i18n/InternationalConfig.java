package cn.flood.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 配置i18n
 */
@Configuration
@ComponentScan(basePackages = {"cn.flood.aop"})
public class InternationalConfig {

//    @Value("${spring.messages.basename}")
    private String basename = "i18n/messages";

    @Primary
    @Bean(name = "floodMessageSource")
    public ResourceBundleMessageSource getMessageResource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        return messageSource;
    }

    @Bean(name = "localeParser")
    public LocaleParser getLocaleParser(@Autowired @Qualifier("floodMessageSource") MessageSource messageSource){
        LocaleParser localeParser = new LocaleParser(messageSource);
        return localeParser;
    }
}
