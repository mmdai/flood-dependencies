package cn.flood.cloud.seata.config;

import cn.flood.cloud.seata.props.SeataProperties;
import cn.flood.support.YamlPropertySourceFactory;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/13 12:27
 */
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:cloud-seata.yml")
@EnableConfigurationProperties(SeataProperties.class)
@AllArgsConstructor
@ConditionalOnBean(DataSource.class)
@EnableAutoDataSourceProxy
@Slf4j
public class SeataConfiguration {

    @Autowired
    public DataSource dataSource;

    public static final String undoLogSql = "CREATE TABLE IF NOT EXISTS undo_log(" +
            "`id` bigint(20) NOT NULL AUTO_INCREMENT," +
            "`branch_id` bigint(20) NOT NULL," +
            "`xid` varchar(100) NOT NULL," +
            "`context` varchar(128) NOT NULL," +
            "`rollback_info` longblob NOT NULL," +
            "`log_status` int(11) NOT NULL," +
            "`log_created` datetime NOT NULL," +
            "`log_modified` datetime NOT NULL," +
            "`ext` varchar(100) DEFAULT NULL," +
            "PRIMARY KEY (`id`)," +
            "UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)" +
            ")ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";

    public final SeataProperties seataProperties;

    /**
     * 判断当前数据库是否有undo_log 该表，如果没有，
     * 创建该表 undo_log 为seata 记录事务sql执行的记录表 第二阶段时，如果confirm会清除记录，如果是cancel 会根据记录补偿原数据
     */
    @PostConstruct
    public void detectTable() {
        try {
            dataSource.getConnection().prepareStatement(undoLogSql).execute();
        } catch (SQLException e) {
            log.error("创建[seata] undo_log表错误。", e);
        }
    }
}
