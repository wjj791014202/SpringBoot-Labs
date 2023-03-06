package com.request;


import com.kucoin.starter.request.mapping.annotation.CustomPathPrefix;
import feign.MethodMetadata;
import feign.Util;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RequestMapping;

public class CustomPathPrefixSpringMvcContract extends SpringMvcContract {
    public CustomPathPrefixSpringMvcContract() {
    }

    public CustomPathPrefixSpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors) {
        super(annotatedParameterProcessors);
    }

    public CustomPathPrefixSpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors, ConversionService conversionService) {
        super(annotatedParameterProcessors, conversionService);
    }

    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        super.processAnnotationOnMethod(data, methodAnnotation, method);
        if (this.needProcessCustomPathPrefix(method)) {
            RequestMapping requestMapping = (RequestMapping)AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (requestMapping.value().length != 0) {
                CustomPathPrefix customPathPrefix = (CustomPathPrefix)AnnotatedElementUtils.findMergedAnnotation(method, CustomPathPrefix.class);
                String pathValue = Util.emptyToNull(requestMapping.value()[0]);
                String customPrefix = customPathPrefix.value() + (customPathPrefix.version() >= 0 ? String.valueOf(customPathPrefix.version()) : "");
                if (pathValue != null && StringUtils.isNotBlank(customPathPrefix.value()) && !data.template().url().contains(customPrefix)) {
                    pathValue = pathValue.startsWith("/") ? pathValue : "/" + pathValue;
                    String pathPrefix = customPrefix.startsWith("/") ? customPrefix : "/" + customPrefix;
                    int pathIndex = data.template().url().indexOf(pathValue);
                    if (pathIndex >= 0) {
                        data.template().insert(pathIndex, pathPrefix);
                    }
                }

            }
        }
    }

    private boolean needProcessCustomPathPrefix(Method method) {
        return AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class) != null && AnnotatedElementUtils.findMergedAnnotation(method, CustomPathPrefix.class) != null;
    }
}

