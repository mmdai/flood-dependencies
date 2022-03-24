package cn.flood.datasource.spring;

import javax.sql.DataSource;

import cn.flood.mybatis.interceptor.SqlLogInterceptor;
import cn.flood.mybatis.plus.EnumConfigurationHelper;
import cn.flood.mybatis.plus.plugins.page.PaginationInterceptor;
import cn.flood.mybatis.plus.plugins.tenant.MultiTenancyProperties;
import cn.flood.mybatis.plus.plugins.tenant.MultiTenancyQueryInterceptor;
import cn.flood.mybatis.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ObjectUtils;

@ComponentScan(basePackages = {"cn.flood.datasource.aop.impl"})
@Configuration
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties({
		MybatisProperties.class
})
public class SqlSessionFactorySpring {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

//	/**
//	 * 配置mapper的扫描，找到所有的mapper.xml映射文件
//	 */
//	@Value("${mybatis.mapper-locations:classpath*:/mapper/**/*Mapper.xml}")
//	private String mapperLocations;

	/**
	 * 加载全局的配置文件
	 */
	@Value("${mybatis.config-location:classpath:/mybatis-config.xml}")  
	private String configLocation;

	@Autowired
	private MybatisProperties mybatisProperties;
	
	@Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
            throws Exception {
		MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);  
        log.debug("---------------------------------------------configLocation:{}", configLocation);
        //设置mybatis-config.xml配置文件位置  
        sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
		if (mybatisProperties.getConfigurationProperties() != null) {
			sessionFactoryBean.setConfigurationProperties(mybatisProperties.getConfigurationProperties());
		}
        try {
			if (ObjectUtils.isEmpty(mybatisProperties.resolveMapperLocations())) {
				mybatisProperties.setMapperLocations(new String[]{"classpath*:/mapper/**/*.xml"});
			}
			sessionFactoryBean.setMapperLocations(mybatisProperties.resolveMapperLocations());
        	//多租户查询设置
			MultiTenancyQueryInterceptor multiTenancyQueryInterceptor = new MultiTenancyQueryInterceptor();
			MultiTenancyProperties multiTenancyProperties = new MultiTenancyProperties();
			// 数据库查询字段（tenant_id为租户id）
			multiTenancyProperties.setMultiTenancyQueryColumn("tenant_id");
			multiTenancyQueryInterceptor.setMultiTenancyProperties(multiTenancyProperties);
        	//添加分页
			sessionFactoryBean.setPlugins(new Interceptor[]{new PaginationInterceptor(), multiTenancyQueryInterceptor, new SqlLogInterceptor()});
			sessionFactoryBean.afterPropertiesSet();
			//添加通用枚举类型
			SqlSessionFactory sessionFactory = sessionFactoryBean.getObject();
			EnumConfigurationHelper.loadEnumHandler(sessionFactory);
			return sessionFactory;
        } catch (Exception e) {
        	 log.error("mybatis sqlSessionFactoryBean create error",e);  
             return null;
        }
    }

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	@Bean // 将数据源纳入spring事物管理
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
