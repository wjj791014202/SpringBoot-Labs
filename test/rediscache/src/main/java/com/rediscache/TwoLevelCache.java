package com.rediscache;

import com.kucoin.starter.rediscache.cache.notify.CacheMessage;
import com.kucoin.starter.rediscache.cache.notify.CacheMessagePublisher;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.NonNull;

public class TwoLevelCache extends AbstractValueAdaptingCache {
    private static final Logger log = LoggerFactory.getLogger(TwoLevelCache.class);
    private final Cache primaryCache;
    private final Cache secondaryCache;
    private CacheMessagePublisher publisher;
    private String topic;

    protected TwoLevelCache(boolean allowNullValues, @NonNull Cache primaryCache, @NonNull Cache secondaryCache) {
        super(allowNullValues);
        this.primaryCache = primaryCache;
        this.secondaryCache = secondaryCache;
    }

    protected Object lookup(@NonNull Object key) {
        ValueWrapper valueWrapper = this.primaryCache.get(key);
        if (valueWrapper != null) {
            log.debug("get cache from primaryCache:{}, key: {}", this.primaryCache, key);
            return valueWrapper.get();
        } else {
            valueWrapper = this.secondaryCache.get(key);
            if (valueWrapper != null) {
                Object val = valueWrapper.get();
                this.primaryCache.put(key, val);
                log.debug("get cache from secondaryCache and put in primaryCache. key : {}", key);
                return val;
            } else {
                log.debug("no value cached for key: {}", key);
                return null;
            }
        }
    }

    @NonNull
    public String getName() {
        return this.primaryCache.getName();
    }

    @NonNull
    public TwoLevelCache getNativeCache() {
        return this;
    }

    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        ValueWrapper valueWrapper = this.get(key);
        return valueWrapper != null ? valueWrapper.get() : this.secondaryCache.get(key, valueLoader);
    }

    public void put(@NonNull Object key, Object value) {
        this.secondaryCache.put(key, value);
        this.primaryCache.put(key, value);
        log.debug("put<K,V>({}, {})", key, value);
    }

    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        ValueWrapper valueWrapper = this.secondaryCache.putIfAbsent(key, value);
        this.primaryCache.putIfAbsent(key, value);
        return valueWrapper;
    }

    public void evict(@NonNull Object key) {
        this.secondaryCache.evict(key);
        this.primaryCache.evict(key);
        this.publish(this.getName(), key, false);
    }

    public void clear() {
        this.secondaryCache.clear();
        this.primaryCache.clear();
        this.publish(this.getName(), "-ALL", true);
    }

    void setPublisher(CacheMessagePublisher publisher) {
        this.publisher = publisher;
    }

    void setTopic(String topic) {
        this.topic = topic;
    }

    private void publish(String name, Object key, boolean clear) {
        if (this.publisher != null && this.topic != null) {
            CacheMessage cacheMessage = new CacheMessage(name, key, clear);
            this.publisher.publish(this.topic, cacheMessage);
        }

    }
}

