package com.kucoin.starter.chaincontext.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.kucoin.starter.chaincontext.ChainRequestContext;

public class UserIdConverter extends ClassicConverter {
    public UserIdConverter() {
    }

    public String convert(ILoggingEvent event) {
        String userId = (String) ChainRequestContext.getCurrentContext().get("X-USER-ID");
        return userId == null ? "-1" : userId;
    }
}
