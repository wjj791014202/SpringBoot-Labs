package com.kucoin.starter.chaincontext.feign;
import feign.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChainContextFeignClientsConfiguration {
    @Autowired
    private Client feignClient;

    public ChainContextFeignClientsConfiguration() {
    }

    @Bean
    public Client chainContextFeignClient() {
        return new ChainContextFeignClient(this.feignClient);
    }
}
