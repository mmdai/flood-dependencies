/**
 * <p>Title: ShardingDataSourceConfig.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2019年7月9日
 * @version 1.0
 */
package cn.flood.db.sharding;

import cn.flood.db.sharding.properties.DruidDbProperties;
import cn.flood.db.sharding.properties.ShardingMasterSlaveProperties;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * <p>Title: ShardingDataSourceConfig</p>  
 * <p>Description: </p>  
 * @author mmdai
 * @date 2019年7月9日
 */
@AutoConfiguration
@EnableConfigurationProperties({ShardingMasterSlaveProperties.class,
    DruidDbProperties.class
})
@ConditionalOnProperty({"sharding.jdbc.data-sources.ds_master.url",
    "sharding.jdbc.master-slave-rule.master-data-source-name"})
public class ShardingDataSourceConfig {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired(required = false)
  private ShardingMasterSlaveProperties shardingMasterSlaveProperties;

  @Autowired
  private DruidDbProperties druidDbProperties;

  @Bean(name = "dataSource") // 只需要纳入动态数据源到spring容器
  public DataSource masterSlaveDataSource() throws SQLException {
    shardingMasterSlaveProperties.getDataSources().forEach((k, v) -> configDataSource(v));
    Map<String, DataSource> dataSourceMap = Maps.newHashMap();
    dataSourceMap.putAll(shardingMasterSlaveProperties.getDataSources());
    DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap,
        new MasterSlaveRuleConfiguration(
            shardingMasterSlaveProperties.getMasterSlaveRule().getName(),
            shardingMasterSlaveProperties.getMasterSlaveRule().getMasterDataSourceName(),
            shardingMasterSlaveProperties.getMasterSlaveRule().getSlaveDataSourceNames(),
            Strings.isNullOrEmpty(
                shardingMasterSlaveProperties.getMasterSlaveRule().getLoadBalanceAlgorithmType())
                ? null : new LoadBalanceStrategyConfiguration(
                shardingMasterSlaveProperties.getMasterSlaveRule().getLoadBalanceAlgorithmType())
        ), new Properties());
    log.info("masterSlaveDataSource config complete");
    return dataSource;
  }

  private void configDataSource(DruidDataSource druidDataSource) {
    //初始化时建立物理连接的个数
    druidDataSource.setInitialSize(druidDbProperties.getInitialSize());
    druidDataSource.setMinIdle(druidDbProperties.getMinIdle());
    druidDataSource.setMaxActive(druidDbProperties.getMaxActive());
    druidDataSource.setMaxWait(druidDbProperties.getMaxWait());
    druidDataSource
        .setTimeBetweenEvictionRunsMillis(druidDbProperties.getTimeBetweenEvictionRunsMillis());
    druidDataSource
        .setMinEvictableIdleTimeMillis(druidDbProperties.getMinEvictableIdleTimeMillis());
    druidDataSource.setValidationQuery(druidDbProperties.getValidationQuery());
    druidDataSource.setQueryTimeout(druidDbProperties.getValidationQueryTimeout());
    druidDataSource.setTestWhileIdle(druidDbProperties.isTestWhileIdle());
    druidDataSource.setTestOnBorrow(druidDbProperties.isTestOnBorrow());
    druidDataSource.setTestOnReturn(druidDbProperties.isTestOnReturn());
    druidDataSource.setRemoveAbandoned(druidDbProperties.isRemoveAbandoned());
    druidDataSource.setRemoveAbandonedTimeout(druidDbProperties.getRemoveAbandonedTimeout());
    druidDataSource.setPoolPreparedStatements(druidDbProperties.isPoolPreparedStatements());
    druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(
        druidDbProperties.getMaxPoolPreparedStatementPerConnectionSize());
    try {
      druidDataSource.setFilters(druidDbProperties.getFilters());
    } catch (SQLException throwables) {
      log.error("datasource Filters is error: {}", throwables);
    }
    Properties properties = new Properties();
    String[] dataProperties = druidDbProperties.getConnectionProperties().split(";");
    for (String proper : dataProperties) {
      properties.setProperty(proper.split("=")[0], proper.split("=")[1]);
    }
    druidDataSource.setConnectProperties(properties);
    druidDataSource.setUseGlobalDataSourceStat(druidDbProperties.isUseGlobalDataSourceStat());
    // 设置druid 连接池非公平锁模式,其实 druid 默认配置为非公平锁，不过一旦设置了maxWait 之后就会使用公平锁模式
    druidDataSource.setUseUnfairLock(true);
  }

}
