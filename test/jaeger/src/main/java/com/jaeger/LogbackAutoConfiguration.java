package com.jaeger;

import ch.qos.logback.classic.LoggerContext;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;

public class LogbackAutoConfiguration {
    public LogbackAutoConfiguration() {
    }

    @PostConstruct
    public void init() {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        lc.addTurboFilter(new KucoinLogbackFilter());
    }
}
