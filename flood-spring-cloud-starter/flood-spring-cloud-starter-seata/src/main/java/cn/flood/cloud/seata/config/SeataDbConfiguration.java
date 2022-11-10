package cn.flood.cloud.seata.config;

import cn.flood.base.core.support.YamlPropertySourceFactory;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/13 12:27
 */
@AutoConfiguration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:cloud-seata.yml")
@AutoConfigureBefore({DataSource.class})
@EnableAutoDataSourceProxy
public class SeataDbConfiguration implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private DataSource dataSource;
    /**
     * seata1.4.2之后，需要回滚的表日期类型不能使用datetime，可以使用timestamp
     */
    public static final String AT_UNDO_LOG_SQL = "CREATE TABLE IF NOT EXISTS undo_log" +
            "(" +
            "`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id'," +
            "`branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id'," +
            "`xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id'," +
            "`context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization'," +
            "`rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info'," +
            "`log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status'," +
            "`log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime'," +
            "`log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime'," +
            "`ext`           VARCHAR(100) DEFAULT NULL COMMENT 'ext'," +
            "PRIMARY KEY (`id`)," +
            "UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)" +
            ")"+
            "ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT ='AT transaction mode undo table'";
    /**
     * seata1.5.0之后，解决tcc 幂等、和反挂等问题
     */
    public static final String TCC_FENCE_SQL = "CREATE TABLE IF NOT EXISTS tcc_fence_log" +
            "(" +
            "`xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id'," +
            "`branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id'," +
            "`action_name`   VARCHAR(64)   NOT NULL COMMENT 'action name'," +
            "`status`        TINYINT       NOT NULL COMMENT 'status(tried:1;committed:2;rollbacked:3;suspended:4)'," +
            "`gmt_create`    DATETIME(3)   NOT NULL COMMENT 'create time'," +
            "`gmt_modified`  DATETIME(3)   NOT NULL COMMENT 'update time'," +
            "`ext`           VARCHAR(100) DEFAULT NULL COMMENT 'ext'," +
            "PRIMARY KEY (`xid`, `branch_id`)," +
            "KEY `idx_gmt_modified` (`gmt_modified`)," +
            "KEY `idx_status` (`status`)" +
            ")"+
            "ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='tcc transaction mode fence table'";


    @PostConstruct
    public void detectTable() {

    }
    /**
     * 判断当前数据库是否有undo_log 该表，如果没有，
     * 创建该表 undo_log 为seata 记录事务sql执行的记录表 第二阶段时，如果confirm会清除记录，如果是cancel 会根据记录补偿原数据
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if(null != dataSource){
            try {
                dataSource.getConnection().prepareStatement(AT_UNDO_LOG_SQL).execute();
            } catch (SQLException e) {
                log.error("创建[seata] undo_log表错误。", e);
            }
            try {
                dataSource.getConnection().prepareStatement(TCC_FENCE_SQL).execute();
            } catch (SQLException e) {
                log.error("创建[seata] tcc_fence_log表错误。", e);
            }
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
