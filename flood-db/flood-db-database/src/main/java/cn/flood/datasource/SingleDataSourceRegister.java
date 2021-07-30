package cn.flood.datasource;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

import cn.flood.datasource.config.DruidDbProperties;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(DruidDbProperties.class)
@ConditionalOnProperty(name="spring.datasource.isSingle",havingValue="true", matchIfMissing = true)
@ConfigurationProperties(prefix="spring.datasource")
@Slf4j
public class SingleDataSourceRegister implements InitializingBean {
	
	@Autowired
	private DruidDbProperties druidDbProperties;
	
	private String url;
	
	private String username;
	
	private String password;
	
	private String driverClassName;
	
	private DataSource datasource;
	

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
	public void afterPropertiesSet() throws Exception {
		if(url != null ){
			log.info("DruidPool-Single {} - Starting...", url);
			DruidDataSource datasource = new DruidDataSource();
			datasource.setUrl(url);
			datasource.setUsername(username);
			datasource.setPassword(password);
			datasource.setDriverClassName(driverClassName);
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
			datasource.setFilters(druidDbProperties.getFilters());
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
			log.info("DruidPool-Single {} - Start completed...", url);
		}
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	

}