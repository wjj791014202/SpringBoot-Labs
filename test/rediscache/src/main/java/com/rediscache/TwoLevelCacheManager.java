package com.rediscache;

import com.kucoin.starter.rediscache.cache.notify.CacheMessage;
import com.kucoin.starter.rediscache.cache.notify.CacheMessagePublisher;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;

public class TwoLevelCacheManager implements CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(TwoLevelCacheManager.class);
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap(16);
    private volatile Set<String> cacheNames = Collections.emptySet();
    private CacheManager primaryCacheManager;
    private CacheManager secondaryCacheManager;
    private CacheMessagePublisher messagePublisher;
    private String topic;
    private String id;

    public TwoLevelCacheManager(CacheManager primaryCacheManager, CacheManager secondaryCacheManager) {
        this.primaryCacheManager = primaryCacheManager;
        this.secondaryCacheManager = secondaryCacheManager;
        this.id = UUID.randomUUID().toString();
    }

    public Cache getCache(@NonNull String name) {
        Cache cache = (Cache)this.cacheMap.get(name);
        if (cache != null) {
            return cache;
        } else {
            synchronized(this.cacheMap) {
                Cache cache = (Cache)this.cacheMap.get(name);
                if (cache == null) {
                    Cache cache1 = this.primaryCacheManager.getCache(name);
                    Cache cache2 = this.secondaryCacheManager.getCache(name);
                    if (ObjectUtils.allNotNull(new Object[]{cache1, cache2})) {
                        cache = this.createCombinedCache(cache1, cache2);
                        this.cacheMap.put(name, cache);
                        this.updateCacheNames(name);
                    }
                }

                return (Cache)cache;
            }
        }
    }

    @NonNull
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    private void updateCacheNames(String name) {
        Set<String> cacheNames = new LinkedHashSet(this.cacheNames.size() + 1);
        cacheNames.addAll(this.cacheNames);
        cacheNames.add(name);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }

    private TwoLevelCache createCombinedCache(Cache primary, Cache secondary) {
        TwoLevelCache twoLevelCache = new TwoLevelCache(true, primary, secondary);
        twoLevelCache.setPublisher(this.messagePublisher);
        twoLevelCache.setTopic(this.topic);
        return twoLevelCache;
    }

    public void setMessagePublisher(CacheMessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
        if (this.messagePublisher != null) {
            this.messagePublisher.setId(this.id);
        }

    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void refreshPrimaryCache(String topic, CacheMessage message) {
        if (!Objects.equals(this.topic, topic)) {
            logger.warn("topic {} does not match original {}", topic, this.topic);
        } else if (Objects.equals(this.id, message.getId())) {
            logger.debug("ignore refresh ");
        } else {
            String cacheName = message.getCacheName();
            Cache cache = this.primaryCacheManager.getCache(cacheName);
            this.evictCache(cache, message.getKey(), message.isAllEntries());
            logger.info("refreshPrimaryCache topic:{} message:{}", topic, message);
        }
    }

    private void evictCache(Cache cache, Object key, boolean all) {
        if (cache != null) {
            if (!all) {
                cache.evict(key);
            } else {
                cache.clear();
            }

        }
    }
}

