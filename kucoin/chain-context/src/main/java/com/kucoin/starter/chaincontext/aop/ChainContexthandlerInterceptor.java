package com.kucoin.starter.chaincontext.aop;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.kucoin.starter.chaincontext.autoconfig.ChainContextPropertiesWrapper;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.kucoin.starter.chaincontext.autoconfig.ChainContextPropertiesWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class ChainContexthandlerInterceptor implements HandlerInterceptor {
    private ChainContextPropertiesWrapper chainContextPropertiesWrapper;

    public ChainContexthandlerInterceptor(ChainContextPropertiesWrapper chainContextPropertiesWrapper) {
        this.chainContextPropertiesWrapper = chainContextPropertiesWrapper;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.chainContextPropertiesWrapper.forEach((key, parser) -> {
            String value = parser.parse(key, request);
            if (value != null) {
                ChainRequestContext.getCurrentContext().put(key, value);
            }

        });
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ChainRequestContext.getCurrentContext() != null) {
            ChainRequestContext.getCurrentContext().unset();
        }

    }
}

