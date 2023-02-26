package com.gray.zuul;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class GrayZuulServiceIdFilter extends ZuulFilter {
    public GrayZuulServiceIdFilter() {
    }

    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.get("serviceId") != null) {
            ChainRequestContext.getCurrentContext().put("SERVICE-ID", ctx.get("serviceId"));
        }

        return null;
    }

    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return 10004;
    }
}