package com.gray.zuul;

import com.gray.GrayProperties;
import com.kucoin.starter.chaincontext.autoconfig.ChainContextAutoConfiguration;
import com.kucoin.starter.chaincontext.autoconfig.ChainContextPropertiesWrapper;
import com.netflix.zuul.http.ZuulServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ZuulServlet.class})
@AutoConfigureAfter({ChainContextAutoConfiguration.class})
@EnableConfigurationProperties({GrayProperties.class})
public class GrayZuulAutoConfiguration {
    @Autowired
    private GrayProperties grayProperties;

    public GrayZuulAutoConfiguration() {
    }

    @Bean
    public GrayZuulVersionPreFilter grayZuulVersionPreFilter(ChainContextPropertiesWrapper wrapper) {
        return new GrayZuulVersionPreFilter(wrapper);
    }

    @ConditionalOnProperty(
            name = {"kucoin.gray.custom.enabled"},
            matchIfMissing = false
    )
    @Bean
    public GrayZuulCustomRoutePreFilter grayZuulCustomRoutePreFilter() {
        return new GrayZuulCustomRoutePreFilter(this.grayProperties);
    }

    @Bean
    public GrayZuulServiceIdFilter grayZuulServiceIdFilter() {
        return new GrayZuulServiceIdFilter();
    }

    @Bean
    public GrayZuulPostFilter grayZuulPostFilter() {
        return new GrayZuulPostFilter();
    }
}

