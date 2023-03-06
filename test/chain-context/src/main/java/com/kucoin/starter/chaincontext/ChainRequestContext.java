package com.kucoin.starter.chaincontext;

import java.util.concurrent.ConcurrentHashMap;

public class ChainRequestContext extends ConcurrentHashMap<String, Object> {
    private static final long serialVersionUID = -5792996927212791314L;
    protected static Class<? extends ChainRequestContext> contextClass = ChainRequestContext.class;
    protected static final ThreadLocal<? extends ChainRequestContext> THREAD_LOCAL = new ThreadLocal<ChainRequestContext>() {
        protected ChainRequestContext initialValue() {
            try {
                return (ChainRequestContext)ChainRequestContext.contextClass.newInstance();
            } catch (Throwable var2) {
                throw new RuntimeException(var2);
            }
        }
    };

    public ChainRequestContext() {
    }

    public static void setContextClass(Class<? extends ChainRequestContext> clazz) {
        contextClass = clazz;
    }

    public static final ChainRequestContext getCurrentContext() {
        return (ChainRequestContext)THREAD_LOCAL.get();
    }

    public void unset() {
        this.clear();
        THREAD_LOCAL.remove();
    }

    public String getString(String key) {
        Object value = this.get(key);
        return value == null ? null : value.toString();
    }
}

