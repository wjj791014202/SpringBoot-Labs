package com.redisid;

import com.kucoin.starter.id.config.RedisIdProperties;
import com.kucoin.starter.id.config.RedisLockProperties;
import com.kucoin.starter.id.generator.IIdGenerator;
import com.kucoin.starter.id.generator.RedisIdGenerator;
import com.kucoin.starter.id.lock.ILockService;
import com.kucoin.starter.id.lock.LockService;
import com.kucoin.starter.redisson.RedissonAutoConfiguration;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnBean({StringRedisTemplate.class, RedissonClient.class})
@AutoConfigureAfter({RedisAutoConfiguration.class, AopAutoConfiguration.class, RedissonAutoConfiguration.class})
@EnableConfigurationProperties({RedisLockProperties.class, RedisIdProperties.class})
public class RedisIdAutoConfiguration {
    public RedisIdAutoConfiguration() {
    }

    @Bean
    public ILockService redisLockService(RedissonClient redissonClient, RedisLockProperties redisLockProperties) {
        return new LockService(redissonClient, redisLockProperties);
    }

    @Bean
    public IIdGenerator redisIdGenerator(StringRedisTemplate stringRedisTemplate, RedisIdProperties redisIdProperties, ILockService redisLockService) {
        return new RedisIdGenerator(stringRedisTemplate, redisIdProperties, redisLockService);
    }
}
