package com.kucoin.starter.eureka;


import com.kucoin.starter.eureka.endpoint.EurekaKucoinOperationEndpoint;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean({EurekaServiceRegistry.class, EurekaClient.class, CloudEurekaInstanceConfig.class, ApplicationInfoManager.class})
@AutoConfigureAfter({EurekaClientAutoConfiguration.class})
public class KucoinEurekaConfigutaion {
    public KucoinEurekaConfigutaion() {
    }

    @Bean
    public EurekaKucoinOperationEndpoint eurekaKucoinOperationEndpoint(ApplicationContext applicationContext, EurekaClient eurekaClient, EurekaServiceRegistry serviceRegistry, CloudEurekaInstanceConfig instanceConfig, ApplicationInfoManager applicationInfoManager, ObjectProvider<HealthCheckHandler> healthCheckHandler) {
        return new EurekaKucoinOperationEndpoint(applicationContext, eurekaClient, serviceRegistry, instanceConfig, applicationInfoManager, healthCheckHandler);
    }
}