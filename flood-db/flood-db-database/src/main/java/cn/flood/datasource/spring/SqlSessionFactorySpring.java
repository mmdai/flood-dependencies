package cn.flood.datasource.spring;

import javax.sql.DataSource;

import cn.flood.mybatis.interceptor.SqlLogInterceptor;
import cn.flood.mybatis.plus.plugins.page.PaginationInterceptor;
import cn.flood.mybatis.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import lombok.extern.slf4j.Slf4j;

@ComponentScan(basePackages = {"cn.flood.datasource.aop.impl"})
@Configuration
@ConditionalOnClass(DataSource.class)
@Slf4j
public class SqlSessionFactorySpring {
	
	/**
	 * 配置mapper的扫描，找到所有的mapper.xml映射文件 
	 */
	@Value("${mybatis.mapper-locations:classpath*:/mapper/**/*Mapper.xml}")  
	private String mapperLocations;  

	/**
	 * 加载全局的配置文件
	 */
	@Value("${mybatis.config-location:classpath:/mybatis-config.xml}")  
	private String configLocation;
	
	@Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
            throws Exception {
		MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);  
        log.debug("---------------------------------------------configLocation:{}", configLocation);
        //设置mybatis-config.xml配置文件位置  
        sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));  
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
        	sessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
        	//添加分页
			sessionFactoryBean.setPlugins(new Interceptor[]{new SqlLogInterceptor(),new PaginationInterceptor()});
			sessionFactoryBean.afterPropertiesSet();
            return sessionFactoryBean.getObject();
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
