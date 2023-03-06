package com.rediscache;

import com.kucoin.starter.rediscache.invocation.InvocationCacheSupport;
import com.kucoin.starter.rediscache.utils.RedisCacheApplicationContextHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;

public class AutoRefreshCache extends RedisCache {
    private RedisTemplate redisTemplate;
    private ReentrantLock lock = new ReentrantLock();
    private long refreshTimeBeforeExpire;

    protected AutoRefreshCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig, RedisTemplate redisTemplate, long refreshTimeBeforeExpire) {
        super(name, cacheWriter, cacheConfig);
        this.redisTemplate = redisTemplate;
        this.refreshTimeBeforeExpire = refreshTimeBeforeExpire;
    }

    public ValueWrapper get(Object key) {
        byte[] keyBytes = this.serializeCacheKey(this.createCacheKey(key));
        byte[] value = this.getNativeCache().get(this.getName(), keyBytes);
        if (value == null) {
            return null;
        } else {
            Object valueObj = this.deserializeCacheValue(value);
            ValueWrapper valueWrapper = this.toValueWrapper(valueObj);
            if (valueWrapper != null) {
                this.refreshCache(new String(keyBytes), this.convertKey(key));
            }

            return valueWrapper;
        }
    }

    private void refreshCache(String cacheKey, String invokeKey) {
        Long ttl = this.redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
        if (ttl != null && ttl > 0L && ttl <= this.refreshTimeBeforeExpire) {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.submit(new Runnable() {
                public void run() {
                    if (AutoRefreshCache.this.lock.tryLock()) {
                        try {
                            Long ttl = AutoRefreshCache.this.redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
                            if (ttl != null && ttl > 0L && ttl <= AutoRefreshCache.this.refreshTimeBeforeExpire) {
                                AutoRefreshCache.this.getInvocationCacheSupport().refreshCacheWithInvocationMethod(AutoRefreshCache.this.getName(), invokeKey);
                            }
                        } finally {
                            AutoRefreshCache.this.lock.unlock();
                        }
                    }

                }
            });
            es.shutdown();
        }

    }

    private InvocationCacheSupport getInvocationCacheSupport() {
        return (InvocationCacheSupport)RedisCacheApplicationContextHelper.getApplicationContext().getBean(InvocationCacheSupport.class);
    }
}
