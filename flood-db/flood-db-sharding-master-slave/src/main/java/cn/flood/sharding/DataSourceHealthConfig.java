package cn.flood.sharding;

import cn.flood.sharding.properties.DruidDbProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorProperties;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@AutoConfiguration
@EnableConfigurationProperties(DruidDbProperties.class)
public class DataSourceHealthConfig extends DataSourceHealthContributorAutoConfiguration {

    private Collection<DataSourcePoolMetadataProvider> metadataProviders;

    private DataSourcePoolMetadataProvider poolMetadataProvider;

    private DruidDbProperties druidDbProperties;

    public DataSourceHealthConfig(ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders,
                                  DruidDbProperties druidDbProperties) {
        super(metadataProviders);
        this.druidDbProperties = druidDbProperties;
    }


    @Override
    public void afterPropertiesSet() {
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(this.metadataProviders);
    }

    @Bean
    @ConditionalOnMissingBean(
            name = {"dbHealthIndicator", "dbHealthContributor"}
    )
    @Override
    public HealthContributor dbHealthContributor(Map<String, DataSource> dataSources, DataSourceHealthIndicatorProperties dataSourceHealthIndicatorProperties) {
        if (dataSourceHealthIndicatorProperties.isIgnoreRoutingDataSources()) {
            Map<String, DataSource> filteredDatasources = (Map)dataSources.entrySet().stream().filter((e) -> {
                return !(e.getValue() instanceof AbstractRoutingDataSource);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return this.createContributor(filteredDatasources);
        } else {
            DataSourceHealthIndicator indicator = (DataSourceHealthIndicator) this.createContributor(dataSources);
            if (!StringUtils.hasText(indicator.getQuery())) {
                indicator.setQuery(druidDbProperties.getValidationQuery());
            }
            return indicator;
        }
    }

    private HealthContributor createContributor(Map<String, DataSource> beans) {
        Assert.notEmpty(beans, "Beans must not be empty");
        return (HealthContributor)(beans.size() == 1 ? this.createContributor((DataSource)beans.values().iterator().next()) : CompositeHealthContributor.fromMap(beans, this::createContributor));
    }

    private HealthContributor createContributor(DataSource source) {
        return new DataSourceHealthIndicator(source, this.getValidationQuery(source));
    }

    private String getValidationQuery(DataSource source) {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider.getDataSourcePoolMetadata(source);
        return poolMetadata != null ? poolMetadata.getValidationQuery() : null;
    }
}
