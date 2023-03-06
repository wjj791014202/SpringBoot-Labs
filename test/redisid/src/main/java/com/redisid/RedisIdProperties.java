package com.redisid;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "kucoin.id.redis"
)
@Data
public class RedisIdProperties {

    @Value("${maxLagTime:5000}")
    private long maxLagTime;
    @Value("${counter.maxAge:31536000000}")
    private long counterMaxAge;
    @Value("${multipler:10000}")
    private long multipler;
    @Value("${retry.wait:1000}")
    private long retryWait;
    @Value("${key.prefix:kucoin:id:generator:}")
    private String keyPrefix;
}
