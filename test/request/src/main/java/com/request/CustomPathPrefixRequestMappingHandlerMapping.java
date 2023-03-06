package com.request;

import com.kucoin.starter.request.mapping.annotation.CustomPathPrefix;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class CustomPathPrefixRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private static final Logger LOG = LoggerFactory.getLogger(CustomPathPrefixRequestMappingHandlerMapping.class);

    public CustomPathPrefixRequestMappingHandlerMapping() {
    }

    protected RequestCondition<CustomPathPrefixCondition> getCustomTypeCondition(Class<?> handlerType) {
        CustomPathPrefix apiVersion = (CustomPathPrefix)AnnotationUtils.findAnnotation(handlerType, CustomPathPrefix.class);
        return this.createCondition(apiVersion);
    }

    protected RequestCondition<CustomPathPrefixCondition> getCustomMethodCondition(Method method) {
        CustomPathPrefix apiVersion = (CustomPathPrefix)AnnotationUtils.findAnnotation(method, CustomPathPrefix.class);
        return this.createCondition(apiVersion);
    }

    protected boolean isHandler(Class<?> beanType) {
        return super.isHandler(beanType);
    }

    private RequestCondition<CustomPathPrefixCondition> createCondition(CustomPathPrefix customPathPrefix) {
        return customPathPrefix == null ? null : new CustomPathPrefixCondition(customPathPrefix.value() + (customPathPrefix.version() >= 0 ? String.valueOf(customPathPrefix.version()) : ""));
    }

    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        if (!this.hasRequestMapping(method)) {
            return null;
        } else if (this.hasRequestMapping(handlerType)) {
            RequestMappingInfo requestMappingInfo = this.createRequestMappingInfo(method, (CustomPathPrefix)null);
            if (requestMappingInfo != null) {
                CustomPathPrefix customPathPrefix = (CustomPathPrefix)AnnotatedElementUtils.findMergedAnnotation(method, CustomPathPrefix.class);
                RequestMappingInfo classRequestMappingInfo = this.createRequestMappingInfo(handlerType, customPathPrefix);
                if (classRequestMappingInfo != null) {
                    requestMappingInfo = classRequestMappingInfo.combine(requestMappingInfo);
                }
            }

            return requestMappingInfo;
        } else {
            CustomPathPrefix customPathPrefix = (CustomPathPrefix)AnnotatedElementUtils.findMergedAnnotation(method, CustomPathPrefix.class);
            return this.createRequestMappingInfo(method, customPathPrefix);
        }
    }

    private boolean hasRequestMapping(AnnotatedElement element) {
        return AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class) != null;
    }

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element, CustomPathPrefix customPathPrefix) {
        RequestMapping requestMapping = (RequestMapping)AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class)element) : this.getCustomMethodCondition((Method)element);
        if (customPathPrefix != null && !StringUtils.isEmpty(customPathPrefix.value())) {
            try {
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(requestMapping);
                Field field = invocationHandler.getClass().getDeclaredField("valueCache");
                field.setAccessible(true);
                Map map = (Map)field.get(invocationHandler);
                String prefix = customPathPrefix.value() + (customPathPrefix.version() >= 0 ? String.valueOf(customPathPrefix.version()) : "");
                String[] paths = new String[requestMapping.path().length];

                for(int i = 0; i < requestMapping.path().length; ++i) {
                    paths[i] = prefix.concat(requestMapping.path()[i].startsWith("/") ? requestMapping.path()[i] : "/" + requestMapping.path()[i]);
                }

                map.put("path", paths);
                String[] values = new String[requestMapping.value().length];

                for(int i = 0; i < requestMapping.value().length; ++i) {
                    values[i] = prefix.concat(requestMapping.value()[i].startsWith("/") ? requestMapping.value()[i] : "/" + requestMapping.value()[i]);
                }

                map.put("value", values);
            } catch (Exception var12) {
                LOG.error("fail to createRequestMappingInfo", var12);
            }

            return this.createRequestMappingInfo(requestMapping, condition);
        } else {
            return this.createRequestMappingInfo(requestMapping, condition);
        }
    }
}
