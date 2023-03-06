package com.redisson;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "spring.redis.redisson"
)
public class RedissonProperties {
    private String config;

    public RedissonProperties() {
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
