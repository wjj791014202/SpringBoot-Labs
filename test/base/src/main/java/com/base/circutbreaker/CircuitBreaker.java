package com.base.circutbreaker;

import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {

    private AtomicInteger failCount;
    private int maxFailCount;
    private AtomicInteger waitRecoveryCount;
    private int maxWaitRecoveryCount;
    private String name;

    public CircuitBreaker() {
        this(5, 5, "default");
    }

    public CircuitBreaker(int maxFailCount, int maxWaitRecoveryCount, String name) {
        this.failCount = new AtomicInteger(0);
        this.waitRecoveryCount = new AtomicInteger(0);
        this.maxFailCount = maxFailCount;
        this.maxWaitRecoveryCount = maxWaitRecoveryCount;
        this.name = name;
    }

    public boolean isBreak() {
        if (this.failCount.get() < this.maxFailCount) {
            return false;
        } else if (this.waitRecoveryCount.get() < this.maxWaitRecoveryCount) {
            this.waitRecoveryCount.incrementAndGet();
            return true;
        } else {
            this.failCount.set(0);
            this.waitRecoveryCount.set(0);
            return false;
        }
    }

    public void fail() {
        if (this.failCount.get() < this.maxFailCount) {
            this.failCount.incrementAndGet();
        }

    }

    public void success() {
        this.failCount.set(0);
        this.waitRecoveryCount.set(0);
    }


    public AtomicInteger getFailCount() {
        return this.failCount;
    }

    public void setFailCount(AtomicInteger failCount) {
        this.failCount = failCount;
    }

    public int getMaxFailCount() {
        return this.maxFailCount;
    }

    public void setMaxFailCount(int maxFailCount) {
        this.maxFailCount = maxFailCount;
    }

    public AtomicInteger getWaitRecoveryCount() {
        return this.waitRecoveryCount;
    }

    public void setWaitRecoveryCount(AtomicInteger waitRecoveryCount) {
        this.waitRecoveryCount = waitRecoveryCount;
    }

    public int getMaxWaitRecoveryCount() {
        return this.maxWaitRecoveryCount;
    }

    public void setMaxWaitRecoveryCount(int maxWaitRecoveryCount) {
        this.maxWaitRecoveryCount = maxWaitRecoveryCount;
    }
}
