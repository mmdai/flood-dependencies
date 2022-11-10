package cn.flood.cloud.log.services.impl;

import cn.flood.cloud.log.config.DruidDbProperties;
import cn.flood.cloud.log.config.LogDbProperties;
import cn.flood.cloud.log.model.Audit;
import cn.flood.cloud.log.services.IAuditService;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 审计日志实现类-数据库
 *
 */
@ConditionalOnProperty(name = "spring.audit-log.log-type", havingValue = "db")
@ConditionalOnClass(JdbcTemplate.class)
@EnableConfigurationProperties({LogDbProperties.class, DruidDbProperties.class})
public class DbAuditServiceImpl implements IAuditService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String INSERT_SQL = " insert into sys_logger " +
            " (application_name, class_name, method_name, user_id, user_name, client_id, action_type, " +
            " request_ip, host_ip, param, operation, timestamp) " +
            " values (?,?,?,?,?,?,?,?,?,?,?,?)";

    private final JdbcTemplate jdbcTemplate;

    public DbAuditServiceImpl(@Autowired(required = false) LogDbProperties logDbProperties,
                              @Autowired(required = false) DruidDbProperties druidDbProperties,
                              @Autowired(required = false) DataSource dataSource) throws SQLException {
        //优先使用配置的日志数据源，否则使用默认的数据源
        if (logDbProperties != null && StringUtils.isNotEmpty(logDbProperties.getUrl())) {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setUrl(logDbProperties.getUrl());
            druidDataSource.setUsername(logDbProperties.getUsername());
            druidDataSource.setPassword(logDbProperties.getPassword());
            druidDataSource.setDriverClassName(logDbProperties.getDriverClassName());
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
        String sql = "CREATE TABLE IF NOT EXISTS `sys_logger`  (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `application_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '应用名',\n" +
                "  `class_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类名',\n" +
                "  `method_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '方法名',\n" +
                "  `user_id` varchar(32) NULL COMMENT '用户id',\n" +
                "  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '用户名',\n" +
                "  `client_id` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '租户id',\n" +
                "  `action_type` tinyint NOT NULL COMMENT '操作类型:0添加,1更新,2删除,3文件下载,4其他',\n" +
                "  `request_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求ip',\n" +
                "  `host_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主机ip',\n" +
                "  `param` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求参数',\n" +
                "  `operation` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作信息',\n" +
                "  `timestamp` timestamp(3) NOT NULL COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`id`) USING BTREE," +
                "  KEY `sys_logger` (`user_id`)\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;";
        this.jdbcTemplate.execute(sql);
    }

    @Async
    @Transactional
    @Override
    public void save(Audit audit) {
        this.jdbcTemplate.update(INSERT_SQL
                , audit.getApplicationName(), audit.getClassName(), audit.getMethodName()
                , audit.getUserId(), audit.getUserName(), audit.getClientId()
                , audit.getActionType().getCode(), audit.getRequestIP(), audit.getHostIP(), audit.getParam(), audit.getOperation(), audit.getTimestamp());
    }
}
