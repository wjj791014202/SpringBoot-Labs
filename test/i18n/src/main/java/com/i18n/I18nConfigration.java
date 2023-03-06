package com.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@EnableConfigurationProperties({I18nMessageProperties.class})
@Import({I18nMessageAdvice.class})
public class I18nConfigration {
    @Autowired
    private I18nMessageProperties messageProperties;

    public I18nConfigration() {
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(this.messageProperties.getBasename());
        messageSource.setCacheMillis(this.messageProperties.getCacheMillis());
        messageSource.setDefaultEncoding(this.messageProperties.getEncoding());
        return messageSource;
    }

    @ConditionalOnBean({MessageSource.class})
    @Bean
    public LocaleMessageResource localeMessageResource() {
        return new LocaleMessageResource();
    }
}

