package com.rediscache;

import com.kucoin.starter.rediscache.cache.TwoLevelCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisMessageListener implements CacheMessageListener, MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageListener.class);
    private RedisTemplate redisTemplate;
    private TwoLevelCacheManager twoLevelCacheManager;

    public RedisMessageListener(RedisTemplate redisTemplate, CacheManager cacheManager) {
        this.redisTemplate = redisTemplate;
        if (cacheManager instanceof TwoLevelCacheManager) {
            this.twoLevelCacheManager = (TwoLevelCacheManager)cacheManager;
        }

    }

    public void onMessage(String topic, CacheMessage message) {
        log.info("onMessage: topic={} message:{} twoLevelCacheManager = ", new Object[]{topic, message, this.twoLevelCacheManager});
        if (this.twoLevelCacheManager != null) {
            this.twoLevelCacheManager.refreshPrimaryCache(topic, message);
        }

    }

    public void onMessage(Message message, byte[] pattern) {
        try {
            CacheMessage cacheMessage = (CacheMessage)this.redisTemplate.getValueSerializer().deserialize(message.getBody());
            this.onMessage(new String(message.getChannel()), cacheMessage);
        } catch (Exception var4) {
            log.warn("process message error. messsage:{}", message, var4);
        }

    }
}
