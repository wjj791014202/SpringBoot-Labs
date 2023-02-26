package com.kucoin.starter.chaincontext.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.slf4j.Marker;

public class UserIdLogbackFilter extends TurboFilter {
    public static final String MDC_USER_ID = "X-UserId";

    public UserIdLogbackFilter() {
    }

    @Override
    public FilterReply decide(Marker marker, ch.qos.logback.classic.Logger logger, Level level, String s, Object[] objects, Throwable throwable) {
        String userId = (String) ChainRequestContext.getCurrentContext().get("X-USER-ID");
        if (StringUtils.isNotBlank(userId)) {
            MDC.put("X-UserId", userId);
        } else {
            MDC.remove("X-UserId");
        }

        return FilterReply.NEUTRAL;
    }

}
