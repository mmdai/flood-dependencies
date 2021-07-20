package cn.flood.db;

import java.io.IOException;

import javax.sql.DataSource;

import cn.flood.mybatis.interceptor.SqlLogInterceptor;
import cn.flood.mybatis.plus.plugins.page.PaginationInterceptor;
import cn.flood.mybatis.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class MybatisConfiguration implements TransactionManagementConfigurer{  
  
	private Logger logger = LoggerFactory.getLogger(this.getClass());
  
    //  配置类型别名  
    @Value("${mybatis.typeAliasesPackage:}")
    private String typeAliasesPackage;
  
    //  配置mapper的扫描，找到所有的mapper.xml映射文件
    @Value("${mybatis.mapper-locations:classpath*:/mapper/**/*Mapper.xml}")
    private String mapperLocations;
  
    //  加载全局的配置文件  
    @Value("${mybatis.config-location:classpath:/mybatis-config.xml}")
    private String configLocation;



    @Autowired
    private DataSource dataSource;
  
    // 提供SqlSeesion
    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory() {
        try {
            MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource);
            // 读取配置
            sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);

            //设置mapper.xml文件所在位置
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
            sessionFactoryBean.setMapperLocations(resources);
            //设置mybatis-config.xml配置文件位置
            sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
            //添加分页
            sessionFactoryBean.setPlugins(new Interceptor[]{new SqlLogInterceptor(),new PaginationInterceptor()});
            sessionFactoryBean.afterPropertiesSet();
            return sessionFactoryBean.getObject();
        } catch (IOException e) {
            logger.error("mybatis resolver mapper*xml is error",e);
            return null;
        } catch (Exception e) {
            logger.error("mybatis sqlSessionFactoryBean create error",e);
            return null;
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    //事务管理
    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }


}
