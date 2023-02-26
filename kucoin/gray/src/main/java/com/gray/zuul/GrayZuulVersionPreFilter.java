package com.gray.zuul;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.kucoin.starter.chaincontext.autoconfig.ChainContextPropertiesWrapper;
import com.kucoin.starter.chaincontext.parser.IRequestKeyParser;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

public class GrayZuulVersionPreFilter extends ZuulFilter {
    private ChainContextPropertiesWrapper wrapper;

    public GrayZuulVersionPreFilter(ChainContextPropertiesWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String version = (String)ctx.getZuulRequestHeaders().get("X-VERSION");
        if (StringUtils.isEmpty(version) && MapUtils.isNotEmpty(this.wrapper) && this.wrapper.get("X-VERSION") != null) {
            version = ((IRequestKeyParser)this.wrapper.get("X-VERSION")).parse("X-VERSION", ctx.getRequest());
        }

        if (StringUtils.isNotEmpty(version)) {
            ChainRequestContext.getCurrentContext().put("X-VERSION", version);
        }

        if (ctx.get("serviceId") != null) {
            ChainRequestContext.getCurrentContext().put("SERVICE-ID", ctx.get("serviceId"));
        }

        return null;
    }

    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return -9994;
    }
}

