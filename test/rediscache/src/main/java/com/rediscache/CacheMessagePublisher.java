package com.rediscache;


public interface CacheMessagePublisher {
    void publish(String topic, CacheMessage message);

    void setId(String id);
}

