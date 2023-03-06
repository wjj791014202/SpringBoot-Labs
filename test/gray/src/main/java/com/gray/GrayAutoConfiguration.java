package com.gray;

import com.gray.ribbon.CustomServicePredicate;
import com.gray.ribbon.GrayRibbonClientsConfiguration;
import com.gray.ribbon.ServiceInfoExtractor;
import com.gray.utils.ApplicationContextHelper;
import com.gray.zuul.GrayZuulAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;

@AutoConfigureAfter({GrayZuulAutoConfiguration.class})
@RibbonClients(
        defaultConfiguration = {GrayRibbonClientsConfiguration.class}
)
@EnableConfigurationProperties({GrayProperties.class})
public class GrayAutoConfiguration {
    @Autowired
    private GrayProperties grayProperties;
    @Autowired
    private SpringClientFactory springClientFactory;

    public GrayAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceInfoExtractor serviceInfoExtractor() {
        return new ServiceInfoExtractor(this.springClientFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @ConditionalOnProperty(
            name = {"kucoin.gray.custom.enabled"},
            matchIfMissing = false
    )
    @Bean
    public CustomServicePredicate customServicePredicate() {
        return new CustomServicePredicate(this.grayProperties);
    }
}

