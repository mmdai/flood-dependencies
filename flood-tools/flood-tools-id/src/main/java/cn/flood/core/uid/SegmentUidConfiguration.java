package cn.flood.core.uid;

import cn.flood.core.uid.bean.UidSegment;
import cn.flood.core.uid.config.DruidDbProperties;
import cn.flood.core.uid.config.UidDbProperties;
import cn.flood.core.uid.extend.strategy.LeafSegmentStrategy;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 分段批量(基于leaf)
 */
//@ConditionalOnProperty(name = "spring.uid.strategy", havingValue = "segment")
@AutoConfiguration
@ConditionalOnClass(JdbcTemplate.class)
@EnableConfigurationProperties({UidDbProperties.class, DruidDbProperties.class})
public class SegmentUidConfiguration {

    private final JdbcTemplate jdbcTemplate;

    public SegmentUidConfiguration(@Autowired(required = false) UidDbProperties uidDbProperties,
                                   @Autowired(required = false) DruidDbProperties druidDbProperties,
                                   @Autowired(required = false) DataSource dataSource) throws SQLException {
        //优先使用配置的日志数据源，否则使用默认的数据源
        if (uidDbProperties != null && StringUtils.isNotEmpty(uidDbProperties.getUrl())) {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setUrl(uidDbProperties.getUrl());
            druidDataSource.setUsername(uidDbProperties.getUsername());
            druidDataSource.setPassword(uidDbProperties.getPassword());
            druidDataSource.setDriverClassName(uidDbProperties.getDriverClassName());
            //初始化时建立物理连接的个数
            druidDataSource.setInitialSize(druidDbProperties.getInitialSize());
            druidDataSource.setMinIdle(druidDbProperties.getMinIdle());
            druidDataSource.setMaxActive(druidDbProperties.getMaxActive());
            druidDataSource.setMaxWait(druidDbProperties.getMaxWait());
            druidDataSource.setTimeBetweenEvictionRunsMillis(druidDbProperties.getTimeBetweenEvictionRunsMillis());
            druidDataSource.setMinEvictableIdleTimeMillis(druidDbProperties.getMinEvictableIdleTimeMillis());
            druidDataSource.setValidationQuery(druidDbProperties.getValidationQuery());
            druidDataSource.setQueryTimeout(druidDbProperties.getValidationQueryTimeout());
            druidDataSource.setTestWhileIdle(druidDbProperties.isTestWhileIdle());
            druidDataSource.setTestOnBorrow(druidDbProperties.isTestOnBorrow());
            druidDataSource.setTestOnReturn(druidDbProperties.isTestOnReturn());
            druidDataSource.setRemoveAbandoned(druidDbProperties.isRemoveAbandoned());
            druidDataSource.setRemoveAbandonedTimeout(druidDbProperties.getRemoveAbandonedTimeout());
            druidDataSource.setPoolPreparedStatements(druidDbProperties.isPoolPreparedStatements());
            druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidDbProperties.getMaxPoolPreparedStatementPerConnectionSize());
            druidDataSource.setFilters(druidDbProperties.getFilters());
            Properties properties = new Properties();
            String[] dataProperties = druidDbProperties.getConnectionProperties().split(";");
            for(String proper : dataProperties){
                properties.setProperty(proper.split("=")[0], proper.split("=")[1]);
            }
            druidDataSource.setConnectProperties(properties);
            druidDataSource.setUseGlobalDataSourceStat(druidDbProperties.isUseGlobalDataSourceStat());
            // 设置druid 连接池非公平锁模式,其实 druid 默认配置为非公平锁，不过一旦设置了maxWait 之后就会使用公平锁模式
            druidDataSource.setUseUnfairLock(true);
            dataSource = druidDataSource;
        }
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS `id_segment`  (\n" +
                "  `biz_tag` varchar(64) NOT NULL COMMENT '业务标识',\n" +
                "  `step` int(11) NOT NULL COMMENT '步长,每一次请求获取的ID个数',\n" +
                "  `max_id` BIGINT(20) NOT NULL DEFAULT 1 COMMENT '最大值',\n" +
                "  `last_update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次修改时间',\n" +
                "  `current_update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '当前修改时间',\n" +
                "  PRIMARY KEY(biz_tag)" +
                ") COMMENT='号段存储表', ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;";
        this.jdbcTemplate.execute(sql);
    }

    @Bean
    public UidSegment getUidSegment(){
        return new UidSegment(new LeafSegmentStrategy(this.jdbcTemplate));
    }
}
