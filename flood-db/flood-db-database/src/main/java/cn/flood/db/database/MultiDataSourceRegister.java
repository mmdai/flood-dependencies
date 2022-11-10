package cn.flood.db.database;

import cn.flood.base.core.Func;
import cn.flood.db.database.config.DruidDbProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.PostConstruct;
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
@AutoConfiguration
@EnableConfigurationProperties(DruidDbProperties.class)
@ConfigurationProperties(prefix = "spring.datasource")
public class MultiDataSourceRegister implements InitializingBean {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DruidDbProperties druidDbProperties;
	
	private List<DataSourceRegisterInfo> sourceConfig = new ArrayList<>();
	
	private List<DataSource> dataSourceList = new ArrayList<>();

	@PostConstruct
	public void setProperties(){
		System.setProperty("druid.mysql.usePingMethod","false");
	}


	@Override
	public void afterPropertiesSet() {

		for(DataSourceRegisterInfo dsource: sourceConfig) {
			//初始化时建立物理连接的个数
			if(dsource.isThreadPool()){
				DruidDataSource ds =  new DruidDataSource();
				ds.setUrl(dsource.getUrl());
				ds.setUsername(dsource.getUsername());
				ds.setPassword(dsource.getPassword());
				ds.setDriverClassName(dsource.getDriverClassName());
				ds.setInitialSize(Func.isNotEmpty(dsource.getInitialSize()) ? dsource.getInitialSize() : druidDbProperties.getInitialSize());
				ds.setMinIdle(Func.isNotEmpty(dsource.getMinIdle()) ? dsource.getMinIdle() :druidDbProperties.getMinIdle());
				ds.setMaxActive(Func.isNotEmpty(dsource.getMaxActive()) ? dsource.getMaxActive() :druidDbProperties.getMaxActive());
				ds.setMaxWait(Func.isNotEmpty(dsource.getMaxWait()) ? dsource.getMaxWait() :druidDbProperties.getMaxWait());
				ds.setTimeBetweenEvictionRunsMillis(Func.isNotEmpty(dsource.getTimeBetweenEvictionRunsMillis()) ? dsource.getTimeBetweenEvictionRunsMillis() :druidDbProperties.getTimeBetweenEvictionRunsMillis());
				ds.setMinEvictableIdleTimeMillis(Func.isNotEmpty(dsource.getMinEvictableIdleTimeMillis()) ? dsource.getMinEvictableIdleTimeMillis() :druidDbProperties.getMinEvictableIdleTimeMillis());
				ds.setValidationQuery(Func.isNotEmpty(dsource.getValidationQuery()) ? dsource.getValidationQuery() :druidDbProperties.getValidationQuery());
				ds.setQueryTimeout(Func.isNotEmpty(dsource.getValidationQueryTimeout()) ? dsource.getValidationQueryTimeout() :druidDbProperties.getValidationQueryTimeout());
				ds.setTestWhileIdle(Func.isNotEmpty(dsource.getTestWhileIdle()) ? dsource.getTestWhileIdle() :druidDbProperties.isTestWhileIdle());
				ds.setTestOnBorrow(Func.isNotEmpty(dsource.getTestOnBorrow()) ? dsource.getTestOnBorrow() :druidDbProperties.isTestOnBorrow());
				ds.setTestOnReturn(Func.isNotEmpty(dsource.getTestOnReturn()) ? dsource.getTestOnReturn() :druidDbProperties.isTestOnReturn());
				ds.setRemoveAbandoned(Func.isNotEmpty(dsource.getRemoveAbandoned()) ? dsource.getRemoveAbandoned() :druidDbProperties.isRemoveAbandoned());
				ds.setRemoveAbandonedTimeout(Func.isNotEmpty(dsource.getRemoveAbandonedTimeout()) ? dsource.getRemoveAbandonedTimeout() :druidDbProperties.getRemoveAbandonedTimeout());
				ds.setPoolPreparedStatements(Func.isNotEmpty(dsource.getPoolPreparedStatements()) ? dsource.getPoolPreparedStatements() :druidDbProperties.isPoolPreparedStatements());
				ds.setMaxPoolPreparedStatementPerConnectionSize(Func.isNotEmpty(dsource.getMaxPoolPreparedStatementPerConnectionSize()) ? dsource.getMaxPoolPreparedStatementPerConnectionSize() :druidDbProperties.getMaxPoolPreparedStatementPerConnectionSize());
				try {
					if(Func.isNotEmpty(dsource.getDriverClassName())
							&& "org.apache.kylin.jdbc.Driver".equalsIgnoreCase(dsource.getDriverClassName())){
						ds.setFilters(druidDbProperties.getKylinFilters());
					}else{
						ds.setFilters(Func.isNotEmpty(dsource.getFilters()) ? dsource.getFilters() :druidDbProperties.getFilters());
					}
				} catch (SQLException throwables) {
					log.error("datasource Filters is error: {}", throwables);
				}
				Properties properties = new Properties();
				String[] dataProperties = druidDbProperties.getConnectionProperties().split(";");
				for(String proper : dataProperties){
					properties.setProperty(proper.split("=")[0], proper.split("=")[1]);
				}
				ds.setConnectProperties(properties);
				ds.setUseGlobalDataSourceStat(Func.isNotEmpty(dsource.getUseGlobalDataSourceStat()) ? dsource.getUseGlobalDataSourceStat() :druidDbProperties.isUseGlobalDataSourceStat());

				// 设置druid 连接池非公平锁模式,其实 druid 默认配置为非公平锁，不过一旦设置了maxWait 之后就会使用公平锁模式
				ds.setUseUnfairLock(true);
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
