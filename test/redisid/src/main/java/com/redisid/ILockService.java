package com.redisid;


public interface ILockService {
    void lock(String key) throws InterruptedException;

    boolean tryLock(String key);

    void unlock(String key);
}