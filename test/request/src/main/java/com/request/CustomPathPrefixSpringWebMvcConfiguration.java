package com.request;

import com.kucoin.starter.request.mapping.spring.CustomPathPrefixRequestMappingHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ConditionalOnProperty(
        name = {"kucoin.requestmapping.spring.enabled"},
        matchIfMissing = true
)
public class CustomPathPrefixSpringWebMvcConfiguration implements WebMvcRegistrations {
    public CustomPathPrefixSpringWebMvcConfiguration() {
    }

    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new CustomPathPrefixRequestMappingHandlerMapping();
    }
}
