package com.kucoin.starter.chaincontext.feign;
import com.netflix.loadbalancer.ILoadBalancer;
import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass({ILoadBalancer.class, Feign.class})
@Configuration
@EnableFeignClients(
        defaultConfiguration = {ChainContextFeignClientsConfiguration.class}
)

public class ChainContextFeignAutoConfiguration {
    public ChainContextFeignAutoConfiguration() {
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new ChainContextRequestInterceptor();
    }
}
