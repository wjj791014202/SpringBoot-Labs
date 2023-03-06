package com.redisid;


import com.kucoin.starter.id.config.RedisLockProperties;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public class LockService implements ILockService {
    private RedissonClient redissonClient;
    private RedisLockProperties redisLockProperties;

    public void lock(String key) throws InterruptedException {
        this.redissonClient.getLock(key).lockInterruptibly(this.redisLockProperties.getLeaseTime(), TimeUnit.MILLISECONDS);
    }

    public boolean tryLock(String key) {
        try {
            return this.redissonClient.getLock(key).tryLock(0L, this.redisLockProperties.getLeaseTime(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException var3) {
            return false;
        }
    }

    public void unlock(String key) {
        RLock lock = this.redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }

    }

    public LockService(final RedissonClient redissonClient, final RedisLockProperties redisLockProperties) {
        this.redissonClient = redissonClient;
        this.redisLockProperties = redisLockProperties;
    }
}

