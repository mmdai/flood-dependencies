package cn.flood.db.sharding;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;

/**
 * 解决 DataSource health check failed org.springframework.dao.InvalidDataAccessApiUsageException: ConnectionCallback; isValid
 * 数据库health健康检查异常问题
 * @author mmdai
 * @version 1.0
 * @date 2022/7/29 11:53
 */
@AutoConfiguration
public class DataSourceHealthConfig {

    /**
     * 解决新版Spring中,健康健康检查用到 sharding jdbc 时,该组件没有完全实现MySQL驱动导致的问题.
     */
    @Bean
    DataSourcePoolMetadataProvider dataSourcePoolMetadataProvider() {
        return dataSource -> {
            if(dataSource instanceof HikariDataSource){
              return new HikariDataSourcePoolMetadata((HikariDataSource) dataSource);
            }
            if(dataSource instanceof DruidDataSource){
                return new DruidDataSourcePoolMetadata((DruidDataSource) dataSource);
            }
            // 这里如果所使用的数据源没有对应的 DataSourcePoolMetadata 实现的话也可以全部使用 NotAvailableDataSourcePoolMetadata
            return new NotAvailableDataSourcePoolMetadata();
        };
    }

    /**
     * 不可用的数据源池元数据.
     */
    private class DruidDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<DruidDataSource> {
        public DruidDataSourcePoolMetadata(DruidDataSource dataSource) {
            super(dataSource);
        }

        @Override
        public Integer getActive() {
            try {
                return this.getDataSource().getActiveCount();
            } catch (Exception var2) {
                return null;
            }
        }
        @Override
        public Integer getIdle() {
            try {
                return this.getDataSource().getMaxIdle();
            } catch (Exception var2) {
                return null;
            }
        }

        @Override
        public Integer getMax() {
            return  this.getDataSource().getMaxActive();
        }
        @Override
        public Integer getMin() {
            return  this.getDataSource().getMinIdle();
        }
        @Override
        public String getValidationQuery() {
            return  this.getDataSource().getValidationQuery();
        }
        @Override
        public Boolean getDefaultAutoCommit() {
            return true;
        }
    }
    /**
     * 不可用的数据源池元数据.
     */
    private static class NotAvailableDataSourcePoolMetadata implements DataSourcePoolMetadata {
        @Override
        public Float getUsage() {
            return null;
        }

        @Override
        public Integer getActive() {
            return null;
        }

        @Override
        public Integer getMax() {
            return null;
        }

        @Override
        public Integer getMin() {
            return null;
        }

        @Override
        public String getValidationQuery() {
            // 该字符串是适用于MySQL的简单查询语句,用于检查检查,其他数据库可能需要更换
            return "select 1";
        }

        @Override
        public Boolean getDefaultAutoCommit() {
            return null;
        }
    }

}
