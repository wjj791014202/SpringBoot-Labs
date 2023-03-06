package com.rediscache;

import java.time.Duration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.lang.Nullable;

public class AutoRefreshRedisCacheManager extends RedisCacheManager {
    public static final String SEPARATOR = "#";
    private RedisTemplate redisTemplate;
    private final RedisCacheConfiguration defaultCacheConfiguration;
    private final RedisCacheWriter cacheWriter;

    public AutoRefreshRedisCacheManager(RedisTemplate redisTemplate, RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
        this.defaultCacheConfiguration = defaultCacheConfiguration;
        this.cacheWriter = cacheWriter;
        this.redisTemplate = redisTemplate;
    }

    protected RedisCache getMissingCache(String name) {
        String[] cacheNames = name.split("#");
        if (cacheNames.length == 3) {
            long expireTime = NumberUtils.toLong(cacheNames[1], 0L);
            long refreshTimeBeforeExpire = NumberUtils.toLong(cacheNames[2], 0L);
            if (!StringUtils.isBlank(cacheNames[0]) && expireTime > 0L && refreshTimeBeforeExpire > 0L && expireTime > refreshTimeBeforeExpire) {
                RedisCacheConfiguration customeCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().serializeKeysWith(SerializationPair.fromSerializer(this.redisTemplate.getKeySerializer())).serializeValuesWith(SerializationPair.fromSerializer(this.redisTemplate.getValueSerializer())).entryTtl(Duration.ofSeconds(expireTime));
                if (!this.defaultCacheConfiguration.getAllowCacheNullValues()) {
                    customeCacheConfiguration = customeCacheConfiguration.disableCachingNullValues();
                }

                if (!this.defaultCacheConfiguration.usePrefix()) {
                    customeCacheConfiguration = customeCacheConfiguration.disableKeyPrefix();
                } else if (StringUtils.isNotBlank(this.defaultCacheConfiguration.getKeyPrefixFor(name))) {
                    customeCacheConfiguration = customeCacheConfiguration.prefixKeysWith(this.defaultCacheConfiguration.getKeyPrefixFor(name));
                }

                return this.createAutoRefreshRedisCache(name, customeCacheConfiguration, refreshTimeBeforeExpire);
            } else {
                return this.createRedisCache(name, this.defaultCacheConfiguration);
            }
        } else {
            return this.createRedisCache(name, this.defaultCacheConfiguration);
        }
    }

    protected RedisCache createAutoRefreshRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig, long refreshTimeBeforeExpire) {
        return new AutoRefreshCache(name, this.cacheWriter, cacheConfig != null ? cacheConfig : this.defaultCacheConfiguration, this.redisTemplate, refreshTimeBeforeExpire);
    }
}
