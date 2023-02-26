package com.kucoin.starter.chaincontext.aop;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.kucoin.starter.chaincontext.autoconfig.ChainContextPropertiesWrapper;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.kucoin.starter.chaincontext.autoconfig.ChainContextPropertiesWrapper;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Order(-2147483648)
@Aspect
public class ChainContextControllerAspect {
    private ChainContextPropertiesWrapper chainContextPropertiesWrapper;

    public ChainContextControllerAspect(ChainContextPropertiesWrapper chainContextPropertiesWrapper) {
        this.chainContextPropertiesWrapper = chainContextPropertiesWrapper;
    }

    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllercut() {
    }

    @Around("controllercut()")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        if (RequestContextHolder.getRequestAttributes() != null && MapUtils.isNotEmpty(this.chainContextPropertiesWrapper)) {
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            this.chainContextPropertiesWrapper.forEach((key, parser) -> {
                String value = parser.parse(key, request);
                if (value != null) {
                    ChainRequestContext.getCurrentContext().put(key, value);
                }

            });
        }

        Object var6;
        try {
            var6 = joinPoint.proceed();
        } finally {
            if (ChainRequestContext.getCurrentContext() != null) {
                ChainRequestContext.getCurrentContext().unset();
            }

        }

        return var6;
    }
}

