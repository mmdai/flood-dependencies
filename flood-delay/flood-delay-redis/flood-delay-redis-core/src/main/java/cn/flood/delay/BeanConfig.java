package cn.flood.delay;

import cn.flood.delay.core.RedisDelayQueueContext;
import cn.flood.delay.mapper.TbDelayJobMapper;
import cn.flood.delay.service.RedisDelayQueue;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@SuppressWarnings("unchecked")
@AutoConfiguration
@MapperScan(basePackages = "cn.flood.delay.mapper")
@EnableConfigurationProperties(DelayProperties.class)
public class BeanConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DelayProperties delayProperties;

    private RedisTemplate redisTemplate;

    private TbDelayJobMapper tbDelayJobMapper;

    private final JdbcTemplate jdbcTemplate;

    public BeanConfig(@Autowired(required = false) DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /******* 接入 RedisDelayQueue  *******/

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        log.info("------>delay redisTemplate 加载完成");
        this.redisTemplate = redisTemplate;
    }

    @Autowired(required = false)
    public void setTbDelayJobMapper(TbDelayJobMapper tbDelayJobMapper) {
        log.info("------>delay tbDelayJobMapper 加载完成");
        this.tbDelayJobMapper = tbDelayJobMapper;
    }

    @PostConstruct
    public void init() {
        /**
         *  sql
         */
        String sql = "CREATE TABLE IF NOT EXISTS `t_delay_queue_job`  (\n" +
                "  `id` varchar(36)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
                "  `topic` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务类型',\n" +
                "  `delay` bigint(20) NULL COMMENT '等待时间(ms)',\n" +
                "  `ttr` bigint(20)  NULL COMMENT '超时时间(ms)',\n" +
                "  `create_time` bigint(20) NULL COMMENT '创建时间',\n" +
                "  `execution_time` bigint(20) NULL COMMENT '用户名',\n" +
                "  `body` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容',\n" +
                "  `status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态',\n" +
                "  `reentry` int NULL COMMENT '重入次数',\n" +
                "  `retry_count` int NULL COMMENT '已重入次数',\n" +
                "  `create_date` timestamp(6) NULL COMMENT '入库时间',\n" +
                "  `update_date` timestamp(6) NULL COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`id`) USING BTREE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;";
        if(null != jdbcTemplate){
            this.jdbcTemplate.execute(sql);
        }
    }

    @Bean
    public RedisDelayQueueContext getRdctx(){

        RedisDelayQueueContext context =  new RedisDelayQueueContext(redisTemplate, delayProperties.getName(), tbDelayJobMapper);
        log.info("RedisDelayQueue");
        return context;
    }

    @Bean
    public RedisDelayQueue getRedisOperation(RedisDelayQueueContext context){
        return context.getRedisDelayQueue();
    }
}
