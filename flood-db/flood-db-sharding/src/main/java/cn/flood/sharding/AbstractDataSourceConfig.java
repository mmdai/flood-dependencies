package cn.flood.sharding;

import cn.flood.sharding.properties.TableRuleProperties;
import com.alibaba.druid.pool.DruidDataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 数据源配置
 *
 * @author
 */
public abstract class AbstractDataSourceConfig {

    /**
     * 根据配置构建数据源
     */
    public DataSource buildDataSource(MultiDataSourceRegister multiDataSourceConfig) throws SQLException {
        return CollectionUtils.isNotEmpty(multiDataSourceConfig.getTableRules()) ?
                buildShardingDataSource(multiDataSourceConfig) : multiDataSourceConfig.getDataSourceList().get(0);
    }
    

    /**
     * 构建shardingsphere数据源
     */
    private DataSource buildShardingDataSource(MultiDataSourceRegister multiDataSourceConfig) throws SQLException {
        //1.配置真实数据源
        Map<String, DataSource> dataSourceMap = buildDataSourceMap(multiDataSourceConfig.getDataSourceList());
        //2.配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = buildShardingRuleConfiguration(multiDataSourceConfig);
        //3.配置其他的属性
        Properties properties = new Properties();
        properties.put("sql.show", multiDataSourceConfig.getSqlshow());
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, properties);
    }

    /**
     * 构建配置真实数据源
     */
    private Map<String, DataSource> buildDataSourceMap(List<DruidDataSource> dataSourceList) {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (int i = 0; i < dataSourceList.size(); i++) {
            //获取目标数据源创建dataSource
            DruidDataSource dataSource = dataSourceList.get(i);
            //将目标数据源放入dataSourceMap
            dataSourceMap.put("ds" + i, dataSource);
        }
        return dataSourceMap;
    }

    /**
     * 配置分片规则
     */
    private ShardingRuleConfiguration buildShardingRuleConfiguration(MultiDataSourceRegister multiDataSourceConfig) {
        //分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        //最后一个db的下标
        int dbLastIndex = multiDataSourceConfig.getDataSources().size() - 1;
        //最后一个table的下标
        int tableLastIndex = multiDataSourceConfig.getTableNum() - 1;

        for (TableRuleProperties tableRule : multiDataSourceConfig.getTableRules()) {
            //构建分片策略实例
            ComplexKeysShardingAlgorithm<Comparable<?>> dbShardingAlgorithm = buildAlgorithmInstance(tableRule.getDbShardingAlgorithm());
            ComplexKeysShardingAlgorithm<Comparable<?>> tableShardingAlgorithm = buildAlgorithmInstance(tableRule.getTableShardingAlgorithm());

            // 配置表规则和分库分表策略
            TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration(tableRule.getLogicTable(), "ds${0.." + dbLastIndex + "}." + tableRule.getLogicTable() + "${0.." + tableLastIndex + "}");
            tableRuleConfiguration.setDatabaseShardingStrategyConfig(new ComplexShardingStrategyConfiguration(tableRule.getDbShardingColumns(), dbShardingAlgorithm));
            tableRuleConfiguration.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration(tableRule.getTableShardingColumns(), tableShardingAlgorithm));
            shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfiguration);
        }
        return shardingRuleConfig;
    }

    /**
     * 构建分片策略实例
     */
    @SuppressWarnings("unchecked")
    private ComplexKeysShardingAlgorithm<Comparable<?>> buildAlgorithmInstance(String shardingAlgorithm) {
        try {
            Class<?> algorithmClass = Class.forName(shardingAlgorithm);
            return (ComplexKeysShardingAlgorithm<Comparable<?>>) algorithmClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("build shardingAlgorithm instance error");
        }
    }

}
