package cn.flood.sharding;

import cn.flood.Func;

import cn.flood.sharding.properties.DataSourceProperties;
import cn.flood.sharding.properties.DruidDbProperties;
import cn.flood.sharding.properties.TableRuleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.alibaba.druid.pool.DruidDataSource;

import javax.annotation.PostConstruct;
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
@ConfigurationProperties(prefix = "sharding.jdbc")
public class MultiDataSourceRegister implements InitializingBean {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DruidDbProperties druidDbProperties;

	/**
	 * 数据源
	 */
	private List<DataSourceProperties> dataSources = new ArrayList<>();


	private List<DruidDataSource> dataSourceList = new ArrayList<>();
	/**
	 * 分片策略
	 */
	private List<TableRuleProperties> tableRules = new ArrayList<>();
	/**
	 * 是否显示 shardingsphere sql执行日志
	 */
	private Boolean sqlShow;
	/**
	 * 每个逻辑库中表的数量
	 */
	private int tableNum;
	/**
	 * 默认数据源
	 */
	private String defaultDataSourceName;

	@PostConstruct
	public void setProperties(){
		System.setProperty("druid.mysql.usePingMethod","false");
	}


	@Override
	public void afterPropertiesSet() {
		for(DataSourceProperties dsource: dataSources) {
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
			try {
				if(Func.isNotEmpty(dsource.getDriverClassName())
						&& "org.apache.kylin.jdbc.Driver".equalsIgnoreCase(dsource.getDriverClassName())){
					ds.setFilters(druidDbProperties.getKylinFilters());
				}else{
					ds.setFilters(druidDbProperties.getFilters());
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
			ds.setUseGlobalDataSourceStat(druidDbProperties.isUseGlobalDataSourceStat());
			// 设置druid 连接池非公平锁模式,其实 druid 默认配置为非公平锁，不过一旦设置了maxWait 之后就会使用公平锁模式
			ds.setUseUnfairLock(true);
			dataSourceList.add(ds);
			log.info("DruidPool-Multi Start completed... {}", dsource.getUrl());
		}

		
	}


	public DruidDbProperties getDruidDbProperties() {
		return druidDbProperties;
	}

	public void setDruidDbProperties(DruidDbProperties druidDbProperties) {
		this.druidDbProperties = druidDbProperties;
	}

	public List<DataSourceProperties> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSourceProperties> dataSources) {
		this.dataSources = dataSources;
	}

	public List<TableRuleProperties> getTableRules() {
		return tableRules;
	}

	public void setTableRules(List<TableRuleProperties> tableRules) {
		this.tableRules = tableRules;
	}

	public Boolean getSqlShow() {
		return sqlShow;
	}

	public void setSqlShow(Boolean sqlShow) {
		this.sqlShow = sqlShow;
	}

	public int getTableNum() {
		return tableNum;
	}

	public void setTableNum(int tableNum) {
		this.tableNum = tableNum;
	}

	public List<DruidDataSource> getDataSourceList() {
		return dataSourceList;
	}

	public void setDataSourceList(List<DruidDataSource> dataSourceList) {
		this.dataSourceList = dataSourceList;
	}

	public String getDefaultDataSourceName() {
		return defaultDataSourceName;
	}

	public void setDefaultDataSourceName(String defaultDataSourceName) {
		this.defaultDataSourceName = defaultDataSourceName;
	}
}
