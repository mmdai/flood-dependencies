package cn.flood.db;

import java.io.IOException;

import javax.sql.DataSource;

import cn.flood.mybatis.interceptor.SqlLogInterceptor;
import cn.flood.mybatis.plus.EnumConfigurationHelper;
import cn.flood.mybatis.plus.plugins.page.PaginationInterceptor;
import cn.flood.mybatis.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@ConditionalOnClass(DataSource.class)
public class SqlSessionFactorySpring{
  
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    //  配置mapper的扫描，找到所有的mapper.xml映射文件
//        @Value("${mybatis.mapperLocations : classpath:com/fei/springboot/dao/*.xml}")
    @Value("${mybatis.mapper-locations:classpath*:/mapper/**/*Mapper.xml}")
    private String mapperLocations;

    //  加载全局的配置文件  
    @Value("${mybatis.config-location:classpath:/mybatis-config.xml}")
    private String configLocation;


    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
            throws Exception {
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        logger.debug("---------------------------------------------configLocation:{}", configLocation);
        //设置mybatis-config.xml配置文件位置
        sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            sessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
            //添加分页
            sessionFactoryBean.setPlugins(new Interceptor[]{new SqlLogInterceptor(),new PaginationInterceptor()});
            sessionFactoryBean.afterPropertiesSet();
            //添加通用枚举类型
            SqlSessionFactory sessionFactory = sessionFactoryBean.getObject();
            EnumConfigurationHelper.loadEnumHandler(sessionFactory);
            return sessionFactory;
        } catch (Exception e) {
            logger.error("mybatis sqlSessionFactoryBean create error",e);
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
