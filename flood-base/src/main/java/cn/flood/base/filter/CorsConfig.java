package cn.flood.base.filter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 跨域配置
 *
 * @author mmdai
 * @date 2021/1/5
 */
@AutoConfiguration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * gateway 网关已经添加 所以注释掉该代码
     * @param registry
     */
//    private final Long ACCESS_CONTROL_MAX_AGE = 3600L;
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns(CorsConfiguration.ALL)//允许跨域的主机地址,*表示所有都可以
//                .allowCredentials(Boolean.TRUE)//是否允许携带cookie,true表示允许
//                .allowedMethods(CorsConfiguration.ALL)//允许跨域的请求方法
//                .exposedHeaders("X-Access-Token", "Flood_Token","channel_id", "cache-control",
//                        "content-language", "client_id", "version", "tenant_id")//允许跨域的请求头
//                .maxAge(ACCESS_CONTROL_MAX_AGE);//单位为秒, 重新预检验跨域的缓存时间,表示该时间内，不用重新检验跨域
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");
        /** swagger-ui 地址 */
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }

}
