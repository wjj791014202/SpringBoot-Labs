package com.redisid;


import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class IdRedisSerializer implements RedisSerializer<Long> {
    private RedisSerializer<String> redisSerializer = new StringRedisSerializer();

    public IdRedisSerializer() {
    }

    public byte[] serialize(Long value) throws SerializationException {
        return this.redisSerializer.serialize(String.valueOf(value));
    }

    public Long deserialize(byte[] bytes) throws SerializationException {
        String value = (String)this.redisSerializer.deserialize(bytes);
        return Long.valueOf(value);
    }
}

