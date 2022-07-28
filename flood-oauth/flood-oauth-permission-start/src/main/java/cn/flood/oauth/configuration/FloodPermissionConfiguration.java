package cn.flood.oauth.configuration;

import cn.flood.jwtp.TenantInterceptor;
import cn.flood.jwtp.TokenInterceptor;
import cn.flood.jwtp.perm.RestUrlPerm;
import cn.flood.jwtp.perm.SimpleUrlPerm;
import cn.flood.jwtp.perm.UrlPerm;
import cn.flood.jwtp.provider.JdbcTokenStore;
import cn.flood.jwtp.provider.RedisTokenStore;
import cn.flood.jwtp.provider.TokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.util.Collection;

/**
 *
 */

@ComponentScan("cn.flood.jwtp.controller")
@EnableConfigurationProperties(FloodPermissionProperties.class)
public class FloodPermissionConfiguration implements WebMvcConfigurer, ApplicationContextAware {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FloodPermissionProperties properties;

    private ApplicationContext applicationContext;

    /**
     * 注入redisTokenStore
     */
    @ConditionalOnProperty(name = "fpermission.store-type", havingValue = "0")
    @Bean
    public TokenStore redisTokenStore() {
        DataSource dataSource = getBean(DataSource.class);
        StringRedisTemplate stringRedisTemplate = getBean(StringRedisTemplate.class);
        if (stringRedisTemplate == null) {
            logger.error("JWTP: StringRedisTemplate is null");
        }
        return new RedisTokenStore(stringRedisTemplate, dataSource);
    }

    /**
     * 注入jdbcTokenStore
     */
    @ConditionalOnProperty(name = "fpermission.store-type", havingValue = "1")
    @Bean
    public TokenStore jdbcTokenStore() {
        DataSource dataSource = getBean(DataSource.class);
        if (dataSource == null) {
            logger.error("JWTP: DataSource is null");
        }
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 注入simpleUrlPerm
     */
    @ConditionalOnProperty(name = "fpermission.url-perm-type", havingValue = "0")
    @Bean
    public UrlPerm simpleUrlPerm() {
        return new SimpleUrlPerm();
    }

    /**
     * 注入restUrlPerm
     */
    @ConditionalOnProperty(name = "fpermission.url-perm-type", havingValue = "1")
    @Bean
    public UrlPerm restUrlPerm() {
        return new RestUrlPerm();
    }

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 获取TokenStore
        TokenStore tokenStore = getBean(TokenStore.class);
        // 给TokenStore添加配置参数
        if (tokenStore != null) {
            tokenStore.setMaxToken(properties.getMaxToken());
            tokenStore.setFindRolesSql(properties.getFindRolesSql());
            tokenStore.setFindPermissionsSql(properties.getFindPermissionsSql());
        } else {
            logger.error("JWTP: Unknown TokenStore");
        }
        // 获取UrlPerm
        UrlPerm urlPerm = getBean(UrlPerm.class);
        // 获取拦截路径
        String[] path = properties.getPath();
        // 获取排除路径
        String[] excludePath = properties.getExcludePath();
        TokenInterceptor interceptor = new TokenInterceptor(tokenStore, urlPerm);
        registry.addInterceptor(interceptor).addPathPatterns(path).excludePathPatterns(excludePath);
        TenantInterceptor tenantInterceptor = new TenantInterceptor();
        registry.addInterceptor(tenantInterceptor);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 配置内容裁决的一些选项
     * @param contentNegotiationConfigurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {

    }

    /**
     * 默认静态资源处理器
     * @param defaultServletHandlerConfigurer
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {

    }

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {

    }
    /**
     *静态资源处理
     **/
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
        registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + properties.getViewResolverPrefix());
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(properties.getViewResolverPrefix());
        resolver.setSuffix(properties.getViewResolverSuffix());
        return resolver;
    }

    /**
     * 解决跨域问题
     * @param corsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
//        corsRegistry.addMapping("/**").allowedOriginPatterns(CorsConfiguration.ALL)
//                .allowCredentials(Boolean.TRUE)
//                .allowedMethods(CorsConfiguration.ALL)
//                .allowedHeaders(CorsConfiguration.ALL)
//                .exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L);
    }

    /**
     * 视图跳转控制器
     * @param viewControllerRegistry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
        viewControllerRegistry.addViewController("/")
                .setViewName("forward:login.html");
        viewControllerRegistry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 获取Bean
     */
    private <T> T getBean(Class<T> clazz) {
        T bean = null;
        Collection<T> beans = applicationContext.getBeansOfType(clazz).values();
        while (beans.iterator().hasNext()) {
            bean = beans.iterator().next();
            if (bean != null) {
                break;
            }
        }
        return bean;
    }
}
