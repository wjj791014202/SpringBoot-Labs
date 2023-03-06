package com.kucoin.starter.chaincontext.resttemplate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestTemplate;

public class ChainContextBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ChainContextBeanPostProcessor.class);

    public ChainContextBeanPostProcessor() {
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            LOG.debug("add ChainContextHttpRequestInterceptor to RestTemplate");
            RestTemplate restTemplate = (RestTemplate)RestTemplate.class.cast(bean);
            restTemplate.getInterceptors().add(new ChainContextHttpRequestInterceptor());
            return restTemplate;
        } else {
            return bean;
        }
    }
}
