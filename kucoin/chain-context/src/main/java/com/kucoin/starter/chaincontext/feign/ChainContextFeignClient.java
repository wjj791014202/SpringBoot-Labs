package com.kucoin.starter.chaincontext.feign;

import com.kucoin.starter.chaincontext.ChainRequestContext;
import java.io.IOException;
import java.net.URI;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Request.Options;
import java.io.IOException;
import java.net.URI;

public class ChainContextFeignClient implements Client {
    private Client client;

    public ChainContextFeignClient(Client client) {
        this.client = client;
    }

    public Response execute(Request request, Options options) throws IOException {
        URI uri = URI.create(request.url());
        ChainRequestContext.getCurrentContext().put("SERVICE-ID", uri.getHost());
        return this.client.execute(request, options);
    }
}
