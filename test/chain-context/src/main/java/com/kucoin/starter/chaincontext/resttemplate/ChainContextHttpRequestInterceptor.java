package com.kucoin.starter.chaincontext.resttemplate;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import java.io.IOException;
import org.apache.commons.collections.MapUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ChainContextHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    public ChainContextHttpRequestInterceptor() {
    }

    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (!ChainRequestContext.getCurrentContext().containsKey("SERVICE-ID")) {
            ChainRequestContext.getCurrentContext().put("SERVICE-ID", request.getURI().getHost());
        }

        if (MapUtils.isNotEmpty(ChainRequestContext.getCurrentContext())) {
            ChainRequestContext.getCurrentContext().forEach((key, value) -> {
                request.getHeaders().set(key, value.toString());
            });
        }

        return execution.execute(request, body);
    }
}
