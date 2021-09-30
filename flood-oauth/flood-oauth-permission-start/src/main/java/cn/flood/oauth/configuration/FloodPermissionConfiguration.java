package cn.flood.oauth.configuration;

import cn.flood.context.SpringContextManager;
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
        TokenStore tokenStore = getBean(TokenStore.class);  // 获取TokenStore
        // 给TokenStore添加配置参数
        if (tokenStore != null) {
            tokenStore.setMaxToken(properties.getMaxToken());
            tokenStore.setFindRolesSql(properties.getFindRolesSql());
            tokenStore.setFindPermissionsSql(properties.getFindPermissionsSql());
        } else {
            logger.error("JWTP: Unknown TokenStore");
        }
        UrlPerm urlPerm = getBean(UrlPerm.class);  // 获取UrlPerm
        String[] path = properties.getPath();  // 获取拦截路径
        String[] excludePath = properties.getExcludePath();  // 获取排除路径
        TokenInterceptor interceptor = new TokenInterceptor(tokenStore, urlPerm);
        registry.addInterceptor(interceptor).addPathPatterns(path).excludePathPatterns(excludePath);
        TenantInterceptor tenantInterceptor = new TenantInterceptor();
        registry.addInterceptor(tenantInterceptor);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /* 配置内容裁决的一些选项*/
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {

    }

    /* 默认静态资源处理器 */
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
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        resourceHandlerRegistry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + properties.getViewResolverPrefix());
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(properties.getViewResolverPrefix());
        resolver.setSuffix(properties.getViewResolverSuffix());
        return resolver;
    }

    /** 解决跨域问题 **/
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**").allowedOrigins("*")
                .allowedMethods("*").allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L);
    }

    /* 视图跳转控制器 */
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
