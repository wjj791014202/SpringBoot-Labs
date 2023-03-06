package com.redisid;


public interface IIdGenerator {
    long nextId(String counterName);

    String nextStringId(String counterName);
}
