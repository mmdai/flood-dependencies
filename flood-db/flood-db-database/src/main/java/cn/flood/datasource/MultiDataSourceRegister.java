package cn.flood.datasource;

import cn.flood.datasource.config.DruidDbProperties;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**  
* <p>Title: MultiDataSourceRegister</p>  
* <p>Description: 多数据源注册</p>  
* @author mmdai  
* @date 2020年8月12日  
*/
@Configuration
@EnableConfigurationProperties(DruidDbProperties.class)
@ConditionalOnProperty(name="spring.datasource.isSingle",havingValue="false", matchIfMissing = true)
@ConfigurationProperties(prefix = "spring.datasource")
@Slf4j
public class MultiDataSourceRegister implements InitializingBean {
	
	@Autowired
	private DruidDbProperties druidDbProperties;
	
	private List<DataSourceRegisterInfo> sourceConfig = new ArrayList<>();
	
	private List<DataSource> dataSourceList = new ArrayList<>();
	

	@Override
	public void afterPropertiesSet() throws Exception {

		for(DataSourceRegisterInfo dsource: sourceConfig) {
			//初始化时建立物理连接的个数
			if(dsource.isThreadPool()){
				DruidDataSource ds =  new DruidDataSource();
				ds.setUrl(dsource.getUrl());
				ds.setUsername(dsource.getUsername());
				ds.setPassword(dsource.getPassword());
				ds.setDriverClassName(dsource.getDriverClassName());
				ds.setInitialSize(druidDbProperties.getInitialSize());
				ds.setMinIdle(druidDbProperties.getMinIdle());
				ds.setMaxActive(druidDbProperties.getMaxActive());
				ds.setMaxWait(druidDbProperties.getMaxWait());
				ds.setTimeBetweenEvictionRunsMillis(druidDbProperties.getTimeBetweenEvictionRunsMillis());
				ds.setMinEvictableIdleTimeMillis(druidDbProperties.getMinEvictableIdleTimeMillis());
				ds.setValidationQuery(druidDbProperties.getValidationQuery());
				ds.setQueryTimeout(druidDbProperties.getValidationQueryTimeout());
				ds.setTestWhileIdle(druidDbProperties.isTestWhileIdle());
				ds.setTestOnBorrow(druidDbProperties.isTestOnBorrow());
				ds.setTestOnReturn(druidDbProperties.isTestOnReturn());
				ds.setRemoveAbandoned(druidDbProperties.isRemoveAbandoned());
				ds.setRemoveAbandonedTimeout(druidDbProperties.getRemoveAbandonedTimeout());
				ds.setPoolPreparedStatements(druidDbProperties.isPoolPreparedStatements());
				ds.setMaxPoolPreparedStatementPerConnectionSize(druidDbProperties.getMaxPoolPreparedStatementPerConnectionSize());
				ds.setFilters(druidDbProperties.getFilters());
				Properties properties = new Properties();
				String[] dataProperties = druidDbProperties.getConnectionProperties().split(";");
				for(String proper : dataProperties){
					properties.setProperty(proper.split("=")[0], proper.split("=")[1]);
				}
				ds.setConnectProperties(properties);
				ds.setUseGlobalDataSourceStat(druidDbProperties.isUseGlobalDataSourceStat());

				// 设置druid 连接池非公平锁模式,其实 druid 默认配置为非公平锁，不过一旦设置了maxWait 之后就会使用公平锁模式
				ds.setUseUnfairLock(true);
				try {
					ds.setFilters(druidDbProperties.getFilters());
				} catch (SQLException e) {
					log.error("DruidDataSource class Filters error:{}", e.getLocalizedMessage());
				}
				dataSourceList.add(ds);
			}else{
				DriverManagerDataSource ds = new DriverManagerDataSource();
				ds.setUrl(dsource.getUrl());
				ds.setUsername(dsource.getUsername());
				ds.setPassword(dsource.getPassword());
				ds.setDriverClassName(dsource.getDriverClassName());
				dataSourceList.add(ds);
			}
			log.info("DruidPool-Multi Start completed... {}", dsource.getUrl());
		}

		
	}
    

	public List<DataSourceRegisterInfo> getSourceConfig() {
		return sourceConfig;
	}

	public void setSourceConfig(List<DataSourceRegisterInfo> sourceConfig) {
		this.sourceConfig = sourceConfig;
	}


	public List<DataSource> getDataSourceList() {
		return dataSourceList;
	}

	public void setDataSourceList(List<DataSource> dataSourceList) {
		this.dataSourceList = dataSourceList;
	}
    
}
