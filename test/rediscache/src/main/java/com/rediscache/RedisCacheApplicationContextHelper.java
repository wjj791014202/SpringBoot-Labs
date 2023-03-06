package com.rediscache;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RedisCacheApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public RedisCacheApplicationContextHelper() {
    }

    public synchronized void setApplicationContext(ApplicationContext appContext) throws BeansException {
        applicationContext = appContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
