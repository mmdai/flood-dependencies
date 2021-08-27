package cn.flood.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 配置i18n
 */
@Configuration
@ComponentScan(basePackages = {"cn.flood.aop"})
public class InternationalConfig {

//    @Value("${spring.messages.basename}")
    private String basename = "i18n/messages";


    @Bean(name = "messageSource")
    @Primary
    public ResourceBundleMessageSource getMessageResource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        return messageSource;
    }

    @Bean(name = "localeParser")
    public LocaleParser getLocaleParser(@Autowired MessageSource messageSource){
        LocaleParser localeParser = new LocaleParser(messageSource);
        return localeParser;
    }
}
