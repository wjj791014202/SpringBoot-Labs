package com.redisid;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "kucoin.id.redis.lock"
)
public class RedisLockProperties {
    @Value("${leaseTime:-1}")
    private long leaseTime;

}
