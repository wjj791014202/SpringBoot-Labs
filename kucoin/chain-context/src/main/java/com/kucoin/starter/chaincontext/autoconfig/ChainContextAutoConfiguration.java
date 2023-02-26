package com.kucoin.starter.chaincontext.autoconfig;

import com.kucoin.starter.chaincontext.hystrix.HystrixChainContextConfiguration;
import com.kucoin.starter.chaincontext.resttemplate.ChainContextHttpRequestInterceptor;
import com.kucoin.starter.chaincontext.aop.ChainContextControllerAspect;
import com.kucoin.starter.chaincontext.hystrix.HystrixChainContextConfiguration;
import com.kucoin.starter.chaincontext.resttemplate.ChainContextHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({HystrixChainContextConfiguration.class})
@EnableConfigurationProperties({ChainContextProperties.class})
public class ChainContextAutoConfiguration implements WebMvcConfigurer {
    @Value("${rest.connection-timeout:3000}")
    private int connectionTimeout;
    @Value("${rest.read-timeout:3000}")
    private int readTimeout;
    @Value("${rest.connection-request-timeout:3000}")
    private int connectionRequestTimeout;

    public ChainContextAutoConfiguration() {
    }

    @Bean(
            initMethod = "initWrapper"
    )
    public ChainContextPropertiesWrapper chainContextPropertiesWrapper(ChainContextProperties chainContextProperties) {
        return new ChainContextPropertiesWrapper(chainContextProperties);
    }

    @ConditionalOnProperty(
            name = {"kucoin.chaincontext.interceptor.enabled"},
            matchIfMissing = true
    )
    @Bean
    public ChainContextControllerAspect chainContextControllerAspect(ChainContextPropertiesWrapper wrapper) {
        return new ChainContextControllerAspect(wrapper);
    }

    @Bean
    @ConfigurationProperties(
            prefix = "resttemplate"
    )
    public SimpleClientHttpRequestFactory customHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @ConditionalOnMissingBean({RestTemplate.class})
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(this.connectionTimeout);
        clientHttpRequestFactory.setReadTimeout(this.readTimeout);
        clientHttpRequestFactory.setConnectionRequestTimeout(this.connectionRequestTimeout);
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.getInterceptors().add(new ChainContextHttpRequestInterceptor());
        return restTemplate;
    }
}

