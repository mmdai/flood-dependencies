package cn.flood;


import cn.flood.captcha.TokenFactory;
import cn.flood.captcha.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaServiceConfiguration {

    @Bean
    public TokenFactory getTokenFactory(){
        return new TokenFactory();
    }

    @Bean
    public TokenService getTokenService(@Autowired TokenFactory tokenFactory){
        return new TokenService(tokenFactory);
    }

}