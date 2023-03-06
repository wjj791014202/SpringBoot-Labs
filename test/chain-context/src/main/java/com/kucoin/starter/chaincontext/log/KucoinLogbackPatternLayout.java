package com.kucoin.starter.chaincontext.log;

import ch.qos.logback.classic.PatternLayout;

public class KucoinLogbackPatternLayout extends PatternLayout {
    public KucoinLogbackPatternLayout() {
    }

    static {
        defaultConverterMap.put("userid", UserIdConverter.class.getName());
    }
}
