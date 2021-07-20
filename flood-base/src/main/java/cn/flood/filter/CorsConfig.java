package cn.flood.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 跨域配置
 *
 * @author mmdai
 * @date 2021/1/5
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(CorsConfiguration.ALL)
                .allowCredentials(Boolean.TRUE)
                .allowedMethods(CorsConfiguration.ALL)
                .exposedHeaders("X-Access-Token", "Flood_Token","channel_id", "cache-control", "content-language")
                .maxAge(3600);
    }

}
