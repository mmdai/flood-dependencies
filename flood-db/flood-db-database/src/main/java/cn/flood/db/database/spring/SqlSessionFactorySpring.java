package cn.flood.db.database.spring;

import cn.flood.base.core.constants.AppConstant;
import cn.flood.db.mybatis.interceptor.SqlLogInterceptor;
import cn.flood.db.mybatis.plus.EnumConfigurationHelper;
import cn.flood.db.mybatis.plus.plugins.page.PaginationInterceptor;
import cn.flood.db.mybatis.plus.plugins.tenant.MultiTenancyProperties;
import cn.flood.db.mybatis.plus.plugins.tenant.MultiTenancyQueryInterceptor;
import cn.flood.db.mybatis.spring.MybatisSqlSessionFactoryBean;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ObjectUtils;

@AutoConfiguration
@Import({
    DataSourceSpring.class
})
@EnableConfigurationProperties({
    MybatisProperties.class
})
@ComponentScan(basePackages = {"cn.flood.db.database.aop.impl"})
public class SqlSessionFactorySpring {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final MybatisProperties mybatisProperties;
  /**
   * 加载全局的配置文件
   */
  @Value("${mybatis.config-location:classpath:/mybatis-config.xml}")
  private String configLocation;

  public SqlSessionFactorySpring(MybatisProperties mybatisProperties) {
    this.mybatisProperties = mybatisProperties;
  }



  /**
   * dev, test, uat 环境打印出BODY
   *
   * @return SqlLogInterceptor
   */
  @Bean("sqlLogInterceptor")
  @Profile({AppConstant.DEV_CODE, AppConstant.TEST_CODE, AppConstant.UAT_CODE})
  public SqlLogInterceptor testLoggingInterceptor() {
    SqlLogInterceptor sqlLogInterceptor = new SqlLogInterceptor();
    sqlLogInterceptor.setLevel(SqlLogInterceptor.Level.BODY);
    return sqlLogInterceptor;
  }


  /**
   * prod 环境打印出BASIC
   *
   * @return SqlLogInterceptor
   */
  @Bean("sqlLogInterceptor")
  @Profile({AppConstant.PROD_CODE})
  public SqlLogInterceptor prodLoggingInterceptor() {
    SqlLogInterceptor sqlLogInterceptor = new SqlLogInterceptor();
    sqlLogInterceptor.setLevel(SqlLogInterceptor.Level.BASIC);
    return sqlLogInterceptor;
  }

  @Bean(name = "sqlSessionFactory")
  public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource,
                                             SqlLogInterceptor interceptor)
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
      sessionFactoryBean.setPlugins(
          new Interceptor[]{new PaginationInterceptor(), multiTenancyQueryInterceptor,
                  interceptor});
      sessionFactoryBean.afterPropertiesSet();
      //添加通用枚举类型
      SqlSessionFactory sessionFactory = sessionFactoryBean.getObject();
      EnumConfigurationHelper.loadEnumHandler(sessionFactory);
      return sessionFactory;
    } catch (Exception e) {
      log.error("mybatis sqlSessionFactoryBean create error", e);
      return null;
    }
  }

  /**
   * @param sqlSessionFactory
   * @return
   */
  @Bean
  public SqlSessionTemplate sqlSessionTemplate(
      @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  /**
   * 将数据源纳入spring事物管理
   *
   * @param dataSource
   * @return
   */
  @Bean
  public DataSourceTransactionManager transactionManager(
      @Qualifier("dataSource") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
