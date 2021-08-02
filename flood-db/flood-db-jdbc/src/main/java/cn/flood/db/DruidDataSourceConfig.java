package cn.flood.db;

import javax.sql.DataSource;

import cn.flood.Func;
import cn.flood.db.config.DataSourceProperties;
import cn.flood.db.config.DruidDbProperties;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.Properties;

/**
 * 
* <p>Title: DruidDataSourceConfig</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年10月11日
 */
@Configuration
@ConditionalOnClass(com.alibaba.druid.pool.DruidDataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource", matchIfMissing = true)
@EnableConfigurationProperties({
        DataSourceProperties.class,
        DruidDbProperties.class
})
@Slf4j
public class DruidDataSourceConfig {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private DruidDbProperties druidDbProperties;

	@Bean(name="dataSource") // 只需要纳入动态数据源到spring容器
    public DataSource dataSource(){
        if(dataSourceProperties.isThreadPool()) {
            DruidDataSource datasource = new DruidDataSource();
            datasource.setUrl(dataSourceProperties.getUrl());
            datasource.setUsername(dataSourceProperties.getUsername());
            datasource.setPassword(dataSourceProperties.getPassword());
            datasource.setDriverClassName(dataSourceProperties.getDriverClassName());
            //初始化时建立物理连接的个数
            datasource.setInitialSize(druidDbProperties.getInitialSize());
            datasource.setMinIdle(druidDbProperties.getMinIdle());
            datasource.setMaxActive(druidDbProperties.getMaxActive());
            datasource.setMaxWait(druidDbProperties.getMaxWait());
            datasource.setTimeBetweenEvictionRunsMillis(druidDbProperties.getTimeBetweenEvictionRunsMillis());
            datasource.setMinEvictableIdleTimeMillis(druidDbProperties.getMinEvictableIdleTimeMillis());
            datasource.setValidationQuery(druidDbProperties.getValidationQuery());
            datasource.setQueryTimeout(druidDbProperties.getValidationQueryTimeout());
            datasource.setTestWhileIdle(druidDbProperties.isTestWhileIdle());
            datasource.setTestOnBorrow(druidDbProperties.isTestOnBorrow());
            datasource.setTestOnReturn(druidDbProperties.isTestOnReturn());
            datasource.setRemoveAbandoned(druidDbProperties.isRemoveAbandoned());
            datasource.setRemoveAbandonedTimeout(druidDbProperties.getRemoveAbandonedTimeout());
            datasource.setPoolPreparedStatements(druidDbProperties.isPoolPreparedStatements());
            datasource.setMaxPoolPreparedStatementPerConnectionSize(druidDbProperties.getMaxPoolPreparedStatementPerConnectionSize());
            try {
                if(Func.isNotEmpty(dataSourceProperties.getDriverClassName())
                        && dataSourceProperties.getDriverClassName().equalsIgnoreCase("org.apache.kylin.jdbc.Driver")){
                    datasource.setFilters("config");
                }else{
                    datasource.setFilters(druidDbProperties.getFilters());
                }
            } catch (SQLException throwables) {
                log.error("datasource Filters is error: {}", throwables);
            }
            Properties properties = new Properties();
            String[] dataProperties = druidDbProperties.getConnectionProperties().split(";");
            for (String proper : dataProperties) {
                properties.setProperty(proper.split("=")[0], proper.split("=")[1]);
            }
            datasource.setConnectProperties(properties);
            datasource.setUseGlobalDataSourceStat(druidDbProperties.isUseGlobalDataSourceStat());
            // 设置druid 连接池非公平锁模式,其实 druid 默认配置为非公平锁，不过一旦设置了maxWait 之后就会使用公平锁模式
            datasource.setUseUnfairLock(true);
            return datasource;
        }else{
            DriverManagerDataSource datasource = new DriverManagerDataSource();
            datasource.setUrl(dataSourceProperties.getUrl());
            datasource.setUsername(dataSourceProperties.getUsername());
            datasource.setPassword(dataSourceProperties.getPassword());
            datasource.setDriverClassName(dataSourceProperties.getDriverClassName());
            return datasource;
        }
    }
    
    /**
     * 注册一个StatViewServlet
     * @return
     */
    @Bean
    public ServletRegistrationBean druidStatViewServlet(){
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        //添加初始化参数：initParams
        //白名单：
        //servletRegistrationBean.addInitParameter("allow","127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        //servletRegistrationBean.addInitParameter("deny","192.168.1.73");
        //登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername","admin");
        servletRegistrationBean.addInitParameter("loginPassword","admin");
        //是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}