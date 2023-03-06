package com.kucoin.starter.chaincontext.hystrix;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChainContextConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private HystrixConcurrencyStrategy existingConcurrencyStrategy;

    public ChainContextConcurrencyStrategy(HystrixConcurrencyStrategy existingConcurrencyStrategy) {
        this.existingConcurrencyStrategy = existingConcurrencyStrategy;
    }

    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return this.existingConcurrencyStrategy != null ? this.existingConcurrencyStrategy.getBlockingQueue(maxQueueSize) : super.getBlockingQueue(maxQueueSize);
    }

    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
        return this.existingConcurrencyStrategy != null ? this.existingConcurrencyStrategy.getRequestVariable(rv) : super.getRequestVariable(rv);
    }

    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize, HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        return this.existingConcurrencyStrategy != null ? this.existingConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue) : super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
        return this.existingConcurrencyStrategy != null ? this.existingConcurrencyStrategy.getThreadPool(threadPoolKey, threadPoolProperties) : super.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return this.existingConcurrencyStrategy != null ? this.existingConcurrencyStrategy.wrapCallable(new DelegatingChainContextCallable(callable, ChainRequestContext.getCurrentContext())) : super.wrapCallable(new DelegatingChainContextCallable(callable, ChainRequestContext.getCurrentContext()));
    }
}
