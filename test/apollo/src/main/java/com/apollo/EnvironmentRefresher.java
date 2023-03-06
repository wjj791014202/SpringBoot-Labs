package com.apollo;

import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EnvironmentRefresher implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public EnvironmentRefresher() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void refresh(Set<String> changedKeys) {
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));
    }
}
