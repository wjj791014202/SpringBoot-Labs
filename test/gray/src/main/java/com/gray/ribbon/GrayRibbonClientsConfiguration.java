package com.gray.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrayRibbonClientsConfiguration {
    @Autowired(
            required = false
    )
    private IClientConfig config;
    @Autowired(
            required = false
    )
    private CustomServicePredicate customServerPredicate;

    public GrayRibbonClientsConfiguration() {
    }

    @Bean
    public IRule ribbonRule() {
        GrayZoneAvoidanceRule rule = new GrayZoneAvoidanceRule(this.customServerPredicate);
        rule.initWithNiwsConfig(this.config);
        return rule;
    }
}
