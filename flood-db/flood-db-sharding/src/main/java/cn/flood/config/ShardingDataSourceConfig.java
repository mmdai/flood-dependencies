/**  
* <p>Title: ShardingDataSourceConfig.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月9日  
* @version 1.0  
*/  
package cn.flood.config;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import cn.flood.config.properties.ShardingMasterSlaveConfig;
import lombok.extern.slf4j.Slf4j;

/**  
* <p>Title: ShardingDataSourceConfig</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月9日  
*/
@Slf4j
@Configuration
@EnableConfigurationProperties(ShardingMasterSlaveConfig.class)
@ConditionalOnProperty({"sharding.jdbc.data-sources.ds_master.url", 
	"sharding.jdbc.master-slave-rule.master-data-source-name"})
public class ShardingDataSourceConfig {

	@Autowired(required = false)
    private ShardingMasterSlaveConfig shardingMasterSlaveConfig;
	 
    @Bean("dataSource")
    public DataSource masterSlaveDataSource() throws SQLException {
    	shardingMasterSlaveConfig.getDataSources().forEach((k, v) -> configDataSource(v));
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        dataSourceMap.putAll(shardingMasterSlaveConfig.getDataSources());
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, 
        		new MasterSlaveRuleConfiguration(shardingMasterSlaveConfig.getMasterSlaveRule().getName(),
        				shardingMasterSlaveConfig.getMasterSlaveRule().getMasterDataSourceName(),
        				shardingMasterSlaveConfig.getMasterSlaveRule().getSlaveDataSourceNames(),
        				Strings.isNullOrEmpty(
        					shardingMasterSlaveConfig.getMasterSlaveRule().getLoadBalanceAlgorithmType()) ? null : new LoadBalanceStrategyConfiguration(shardingMasterSlaveConfig.getMasterSlaveRule().getLoadBalanceAlgorithmType())
        						), new Properties());
        log.info("masterSlaveDataSource config complete");
        return dataSource;
    }
    
    private void configDataSource(DruidDataSource druidDataSource) {
    	druidDataSource.setInitialSize(initialSize);
    	druidDataSource.setMinIdle(minIdle);
    	druidDataSource.setMaxActive(maxActive);
    	druidDataSource.setMaxWait(maxWait);
    	druidDataSource.setValidationQuery(validationQuery);
    	druidDataSource.setValidationQueryTimeout(validationQueryTimeout);
    	druidDataSource.setTestOnBorrow(testOnBorrow);
    	druidDataSource.setTestOnReturn(testOnReturn);
    	druidDataSource.setTestWhileIdle(testWhileIdle);
    	druidDataSource.setRemoveAbandoned(removeAbandoned);
    	druidDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    	druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    	druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
    	druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
    	try {
            druidDataSource.setFilters(filters);
        } catch (SQLException e) {
            log.error("druid configuration initialization filter", e);
        }
    	druidDataSource.setConnectionProperties(connectionProperties);
    	druidDataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
    }
    @Value(value = "${sharding.jdbc.druid.initial-size:10}")
    private int initialSize;
    @Value(value = "${sharding.jdbc.druid.min-idle:20}")
    private int minIdle;
    @Value(value = "${sharding.jdbc.druid.max-active:120}")
    private int maxActive;
    @Value(value = "${sharding.jdbc.druid.max-wait:30000}")
    private long maxWait;
    @Value(value = "${sharding.jdbc.druid.validation-query:select 1}")
    private String validationQuery;
    @Value(value = "${sharding.jdbc.druid.validation-query-timeout:5}")
    private int validationQueryTimeout;
    @Value(value = "${sharding.jdbc.druid.test-on-borrow:false}")
    private boolean testOnBorrow;
    @Value(value = "${sharding.jdbc.druid.test-on-return:false}")
    private boolean testOnReturn;
    @Value(value = "${sharding.jdbc.druid.test-while-idle:true}")
    private boolean testWhileIdle;
    @Value(value = "${sharding.jdbc.druid.remove-abandoned:true}")
    private boolean removeAbandoned;
    @Value(value = "${sharding.jdbc.druid.remove-abandoned-timeout:120}")
    private int removeAbandonedTimeout;
    @Value(value = "${sharding.jdbc.druid.time-between-eviction-runs-millis:30000}")
    private long timeBetweenEvictionRunsMillis;
    @Value(value = "${sharding.jdbc.druid.min-evictable-idle-time-millis:60000}")
    private long minEvictableIdleTimeMillis;
    @Value(value = "${sharding.jdbc.druid.pool-prepared-statements:false}")
    private boolean poolPreparedStatements;
    @Value(value = "${sharding.jdbc.druid.max-pool-prepared-statement-per-connection-size:20}")
    private int maxPoolPreparedStatementPerConnectionSize;
    @Value(value = "${sharding.jdbc.druid.filters:stat,wall}")
    private String filters;
    @Value(value = "${sharding.jdbc.druid.connection-properties:druid.stat.mergeSql=true;druid.stat.slowSqlMillis=10}")
    private String connectionProperties;
    @Value(value = "${sharding.jdbc.druid.use-global-data-source-stat:true}")
    private boolean useGlobalDataSourceStat;
}
