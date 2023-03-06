package com.shardingjdbc;

import com.google.common.base.Preconditions;
import com.kucoin.starter.shardingjdbc.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import com.kucoin.starter.shardingjdbc.sharding.SpringBootShardingRuleConfigurationProperties;
import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.exception.ShardingJdbcException;
import io.shardingjdbc.core.util.DataSourceUtil;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AutoConfigureBefore({DataSourcePoolMetadataProvidersConfiguration.class, DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({SpringBootShardingRuleConfigurationProperties.class, SpringBootMasterSlaveRuleConfigurationProperties.class})
public class ShardingSpringBootConfiguration implements EnvironmentAware {
    @Autowired
    private SpringBootShardingRuleConfigurationProperties shardingProperties;
    @Autowired
    private SpringBootMasterSlaveRuleConfigurationProperties masterSlaveProperties;
    private final Map<String, DataSource> dataSourceMap = new HashMap();

    public ShardingSpringBootConfiguration() {
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        return null == this.masterSlaveProperties.getMasterDataSourceName() ? ShardingDataSourceFactory.createDataSource(this.dataSourceMap, this.shardingProperties.getShardingRuleConfiguration(), this.shardingProperties.getConfigMap(), this.shardingProperties.getProps()) : MasterSlaveDataSourceFactory.createDataSource(this.dataSourceMap, this.masterSlaveProperties.getMasterSlaveRuleConfiguration(), this.masterSlaveProperties.getConfigMap());
    }

    public void setEnvironment(final Environment environment) {
        String names = environment.getProperty("sharding.jdbc.datasource.names");
        if (StringUtils.isNotBlank(names)) {
            Stream.of(names.split(",")).forEach((db) -> {
                Binder.get(environment).bind("sharding.jdbc.datasource." + db, Bindable.mapOf(String.class, Object.class)).ifBound((dataSourceProps) -> {
                    try {
                        Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
                        DataSource dataSource = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
                        this.dataSourceMap.put(db, dataSource);
                    } catch (ReflectiveOperationException var4) {
                        throw new ShardingJdbcException("Can't find datasource type!", var4);
                    }
                });
            });
        }

    }
}
