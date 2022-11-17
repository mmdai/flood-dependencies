package cn.flood.tools.uid;


import cn.flood.tools.uid.baidu.UidGenerator;
import cn.flood.tools.uid.baidu.impl.CachedUidGenerator;
import cn.flood.tools.uid.bean.UidBaidu;
import cn.flood.tools.uid.config.DruidDbProperties;
import cn.flood.tools.uid.config.UidDbProperties;
import cn.flood.tools.uid.extend.strategy.BaiduUidStrategy;
import cn.flood.tools.uid.extend.strategy.IUidStrategy;
import cn.flood.tools.uid.worker.DisposableWorkerIdAssigner;
import cn.flood.tools.uid.worker.WorkerIdAssigner;
import cn.flood.tools.uid.worker.dao.WorkerNodeDAO;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

//@ConditionalOnProperty(name = "spring.uid.strategy", havingValue = "baidu")
@AutoConfiguration
@ConditionalOnClass(JdbcTemplate.class)
@EnableConfigurationProperties({UidDbProperties.class, DruidDbProperties.class})
public class BaiduUidConfiguration {

    private final JdbcTemplate jdbcTemplate;

    private DisposableWorkerIdAssigner disposableWorkerIdAssigner;

    private IUidStrategy uidStrategy;

    private WorkerNodeDAO workerNodeDAO;

    private UidGenerator uidGenerator;

    public BaiduUidConfiguration(@Autowired(required = false) UidDbProperties uidDbProperties,
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
        this.workerNodeDAO =  new WorkerNodeDAO(this.jdbcTemplate);
        this.disposableWorkerIdAssigner = new DisposableWorkerIdAssigner();
        this.uidGenerator = new CachedUidGenerator();
        ((CachedUidGenerator)this.uidGenerator).setWorkerIdAssigner(this.disposableWorkerIdAssigner);
        this.uidStrategy = new BaiduUidStrategy();
    }

    @PostConstruct
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS `worker_node`  (\n" +
                "  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增 id',\n" +
                "  `host_name` VARCHAR(64) NOT NULL COMMENT '主机名',\n" +
                "  `port` VARCHAR(64) NOT NULL COMMENT '端口',\n" +
                "  `type` INT NOT NULL COMMENT '节点类型: ACTUAL or CONTAINER',\n" +
                "  `launch_date` DATE NOT NULL COMMENT '启动时间',\n" +
                "  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  PRIMARY KEY(id)" +
                ")COMMENT='DB WorkerID Assigner for UID Generator',ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic";
        this.jdbcTemplate.execute(sql);
    }

    @Bean
    public WorkerNodeDAO getWorkerNodeDAO(){
        if(this.workerNodeDAO !=null){
            return this.workerNodeDAO;
        }
        return new WorkerNodeDAO(this.jdbcTemplate);
    }

    @Bean
    @ConditionalOnClass(WorkerNodeDAO.class)
    public WorkerIdAssigner getDisposableWorkerIdAssigner(){
        if(this.disposableWorkerIdAssigner!=null){
            return this.disposableWorkerIdAssigner;
        }
        this.disposableWorkerIdAssigner = new DisposableWorkerIdAssigner();
        return this.disposableWorkerIdAssigner;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnClass(WorkerIdAssigner.class)
    public UidGenerator getCachedUidGenerator(){
        if(this.uidGenerator!=null){
            return this.uidGenerator;
        }
        CachedUidGenerator uidGenerator = new CachedUidGenerator();
        uidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        return uidGenerator;
    }

    @Bean
    @ConditionalOnClass(UidGenerator.class)
    public IUidStrategy getBaiduUidStrategy(){
        if(this.uidStrategy!=null){
            return this.uidStrategy;
        }
        uidStrategy = new BaiduUidStrategy();
        return uidStrategy;
    }


    @Bean
    public UidBaidu getUidBaidu(){
        return new UidBaidu(uidStrategy);
    }
}
