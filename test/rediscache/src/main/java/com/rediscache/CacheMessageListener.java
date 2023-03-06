package com.rediscache;

public interface CacheMessageListener {
    void onMessage(String topic, CacheMessage message);
}
