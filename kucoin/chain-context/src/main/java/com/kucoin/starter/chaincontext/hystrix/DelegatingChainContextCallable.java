package com.kucoin.starter.chaincontext.hystrix;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import java.util.Map;
import java.util.concurrent.Callable;

public final class DelegatingChainContextCallable<V> implements Callable<V> {

    private final Callable<V> delegate;
    private Map<String, Object> chaincontextAttributes;

    public DelegatingChainContextCallable(Callable<V> delegate, Map<String, Object> chaincontextAttributes) {
        this.delegate = delegate;
        this.chaincontextAttributes = chaincontextAttributes;
    }

    public V call() throws Exception {
        V var1;
        try {
            ChainRequestContext.getCurrentContext().putAll(this.chaincontextAttributes);
            var1 = this.delegate.call();
        } finally {
            ChainRequestContext.getCurrentContext().unset();
        }

        return var1;
    }
}
