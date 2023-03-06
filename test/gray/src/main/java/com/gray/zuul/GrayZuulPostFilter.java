package com.gray.zuul;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

public class GrayZuulPostFilter extends ZuulFilter {
    public GrayZuulPostFilter() {
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() throws ZuulException {
        if (ChainRequestContext.getCurrentContext() != null) {
            ChainRequestContext.getCurrentContext().remove("X-VERSION");
        }

        return null;
    }

    public String filterType() {
        return "post";
    }

    public int filterOrder() {
        return 999;
    }
}

