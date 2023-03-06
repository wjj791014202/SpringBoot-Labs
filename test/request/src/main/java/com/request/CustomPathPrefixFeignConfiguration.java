package com.request;

import com.google.common.collect.Lists;
import com.kucoin.starter.request.mapping.feign.CustomPathPrefixSpringMvcContract;
import com.kucoin.starter.request.mapping.feign.FormBodyEncoder;
import com.kucoin.starter.request.mapping.feign.FormBodyParameterProcessor;
import feign.Contract;
import feign.Feign;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.cloud.openfeign.annotation.PathVariableParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestHeaderParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestParamParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

@Configuration
@ConditionalOnClass({Feign.class})
@AutoConfigureBefore({FeignClientsConfiguration.class})
public class CustomPathPrefixFeignConfiguration {
    @Autowired(
            required = false
    )
    private List<FeignFormatterRegistrar> feignFormatterRegistrars = new ArrayList();
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    public CustomPathPrefixFeignConfiguration() {
    }

    @ConditionalOnClass({FormEncoder.class})
    @Bean
    public Encoder feignEncoder() {
        return new FormBodyEncoder(new SpringEncoder(this.messageConverters));
    }

    @Bean
    public Contract feignContract() {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        Iterator var2 = this.feignFormatterRegistrars.iterator();

        while(var2.hasNext()) {
            FeignFormatterRegistrar feignFormatterRegistrar = (FeignFormatterRegistrar)var2.next();
            feignFormatterRegistrar.registerFormatters(conversionService);
        }

        List<AnnotatedParameterProcessor> annotatedArgumentResolvers = Lists.newArrayList(new AnnotatedParameterProcessor[]{new PathVariableParameterProcessor(), new RequestParamParameterProcessor(), new RequestHeaderParameterProcessor(), new FormBodyParameterProcessor()});
        return new CustomPathPrefixSpringMvcContract(annotatedArgumentResolvers, conversionService);
    }
}
