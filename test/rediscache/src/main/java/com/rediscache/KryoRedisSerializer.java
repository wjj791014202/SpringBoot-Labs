package com.rediscache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    private Kryo kryo = new Kryo();

    public KryoRedisSerializer() {
    }

    public byte[] serialize(Object t) throws SerializationException {
        byte[] buffer = new byte[4096];
        Output output = new Output(buffer);
        this.kryo.writeClassAndObject(output, t);
        return output.toBytes();
    }

    public T deserialize(byte[] bytes) throws SerializationException {
        Input input = new Input(bytes);
        return this.kryo.readClassAndObject(input);
    }
}
