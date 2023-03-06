package com.shardingjdbc;

@ConfigurationProperties(
        prefix = "sharding.jdbc.config.masterslave"
)
public class SpringBootMasterSlaveRuleConfigurationProperties extends YamlMasterSlaveRuleConfiguration {
    public SpringBootMasterSlaveRuleConfigurationProperties() {
    }
}
