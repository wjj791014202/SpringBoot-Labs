package com.gray.zuul;

import com.gray.GrayProperties;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;

public class GrayZuulCustomRoutePreFilter extends ZuulFilter {
    private GrayProperties grayProperties;

    public GrayZuulCustomRoutePreFilter(GrayProperties grayProperties) {
        this.grayProperties = grayProperties;
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String customRouteKey = this.grayProperties.getCustom().getRequestKey();
        String requestRouteValue = ctx.getRequest().getHeader(customRouteKey);
        if (StringUtils.isEmpty(requestRouteValue)) {
            requestRouteValue = ctx.getRequest().getParameter(customRouteKey);
        }

        if (StringUtils.isNotEmpty(requestRouteValue)) {
            ChainRequestContext.getCurrentContext().put(customRouteKey, requestRouteValue);
            ctx.getRequest().setAttribute("symbol",requestRouteValue);
        }

        return null;
    }

    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return -9993;
    }
}

