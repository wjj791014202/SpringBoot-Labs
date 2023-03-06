package com.rediscache;

import com.kucoin.starter.rediscache.utils.RedisCacheApplicationContextHelper;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MethodInvoker;

public class InvocationCacheSupport implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(InvocationCacheSupport.class);
    private static Map<String, Set<InvocationMethod>> INVOCATION_MAP;
    private CacheManager cacheManager;

    public InvocationCacheSupport(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void refreshCacheWithInvocationMethod(String cacheName, String invokeKey) {
        if (!CollectionUtils.isEmpty((Collection)INVOCATION_MAP.get(cacheName))) {
            Optional<InvocationMethod> invocationMethodOptional = ((Set)INVOCATION_MAP.get(cacheName)).stream().filter((invocation) -> {
                return invokeKey.equals(invocation.getKey().toString());
            }).findFirst();
            if (invocationMethodOptional.isPresent()) {
                try {
                    Object value = this.invokeCacheMethod((InvocationMethod)invocationMethodOptional.get());
                    this.cacheManager.getCache(cacheName).put(invokeKey, value);
                } catch (Exception var5) {
                    LOG.error("fail to invoke refresh method with cacheName: {} and key: {}", new Object[]{cacheName, invokeKey, var5});
                }
            }
        }

    }

    public void addInvocation(Object target, Method method, Object[] params, Set<String> annotatedCacheNames, String cacheKey) {
        Object key = StringUtils.isEmpty(cacheKey) ? this.getKeyGenerator().generate(target, method, params) : cacheKey;
        InvocationMethod invocation = new InvocationMethod(key, target, method, params);

        String cacheName;
        for(Iterator var8 = annotatedCacheNames.iterator(); var8.hasNext(); ((Set)INVOCATION_MAP.get(cacheName)).add(invocation)) {
            cacheName = (String)var8.next();
            Set<InvocationMethod> sets = (Set)INVOCATION_MAP.get(cacheName);
            if (sets == null) {
                Set<InvocationMethod> sets = new CopyOnWriteArraySet();
                INVOCATION_MAP.put(cacheName, sets);
            }
        }

    }

    private Object invokeCacheMethod(InvocationMethod invocationMethod) throws Exception {
        MethodInvoker invoker = new MethodInvoker();
        invoker.setTargetObject(invocationMethod.getTarget());
        invoker.setArguments(invocationMethod.getParams());
        invoker.setTargetMethod(invocationMethod.getMethod().getName());
        invoker.prepare();
        return invoker.invoke();
    }

    public void afterPropertiesSet() throws Exception {
        INVOCATION_MAP = new ConcurrentHashMap(this.cacheManager.getCacheNames().size());
        this.cacheManager.getCacheNames().forEach((cacheName) -> {
            INVOCATION_MAP.put(cacheName, new CopyOnWriteArraySet());
        });
    }

    private KeyGenerator getKeyGenerator() {
        return (KeyGenerator)RedisCacheApplicationContextHelper.getApplicationContext().getBean(KeyGenerator.class);
    }
}
