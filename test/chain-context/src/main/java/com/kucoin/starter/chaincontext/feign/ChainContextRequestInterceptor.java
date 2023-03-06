package com.kucoin.starter.chaincontext.feign;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.collections.MapUtils;

public class ChainContextRequestInterceptor implements RequestInterceptor {
    public ChainContextRequestInterceptor() {
    }

    public void apply(RequestTemplate template) {
        if (MapUtils.isNotEmpty(ChainRequestContext.getCurrentContext())) {
            ChainRequestContext.getCurrentContext().forEach((key, value) -> {
                template.header(key, new String[]{value.toString()});
            });
        }

    }
}