package cn.flood.datasource;

import javax.sql.DataSource;

import cn.flood.Func;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

import cn.flood.datasource.config.DruidDbProperties;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(DruidDbProperties.class)
@ConditionalOnProperty(name="spring.datasource.isSingle",havingValue="true", matchIfMissing = true)
@ConfigurationProperties(prefix="spring.datasource")
public class SingleDataSourceRegister implements InitializingBean {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DruidDbProperties druidDbProperties;
	
	private String url;
	
	private String username;
	
	private String password;
	
	private String driverClassName;
	
	private DataSource datasource;
	/**
	 * 是否需要复用druid线程池， 因为Kylin不使用druid
	 */
	private boolean threadPool = true;
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}


	@Override
	public void afterPropertiesSet() {
		if(url != null ){
			log.info("DruidPool-Single {} - Starting...", url);
			//初始化时建立物理连接的个数
			if(threadPool){
				DruidDataSource datasource = new DruidDataSource();
				datasource.setUrl(url);
				datasource.setUsername(username);
				datasource.setPassword(password);
				datasource.setDriverClassName(driverClassName);
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
					if(Func.isNotEmpty(driverClassName)
							&& driverClassName.equalsIgnoreCase("org.apache.kylin.jdbc.Driver")){
						datasource.setFilters(druidDbProperties.getKylinFilters());
					}else{
						datasource.setFilters(druidDbProperties.getFilters());
					}
				} catch (SQLException throwables) {
					log.error("datasource Filters is error: {}", throwables);
				}
				Properties properties = new Properties();
				String[] dataProperties = druidDbProperties.getConnectionProperties().split(";");
				for(String proper : dataProperties){
					properties.setProperty(proper.split("=")[0], proper.split("=")[1]);
				}
				datasource.setConnectProperties(properties);
				datasource.setUseGlobalDataSourceStat(druidDbProperties.isUseGlobalDataSourceStat());
				// 设置druid 连接池非公平锁模式,其实 druid 默认配置为非公平锁，不过一旦设置了maxWait 之后就会使用公平锁模式
				datasource.setUseUnfairLock(true);
				this.datasource = datasource;
			} else {
				DriverManagerDataSource dataSource = new DriverManagerDataSource();
				dataSource.setDriverClassName(driverClassName);
				dataSource.setUrl(url);
				dataSource.setUsername(username);
				dataSource.setPassword(password);
				this.datasource = dataSource;
			}
			log.info("DruidPool-Single {} - Start completed...", url);
		}
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public boolean isThreadPool() {
		return threadPool;
	}

	public void setThreadPool(boolean threadPool) {
		this.threadPool = threadPool;
	}

}
