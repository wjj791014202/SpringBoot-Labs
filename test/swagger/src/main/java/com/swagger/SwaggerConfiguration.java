package com.swagger;

import com.kucoin.base.result.CommonCode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties({SwaggerProperties.class})
public class SwaggerConfiguration {
    @Autowired
    private SwaggerProperties swaggerProperties;
    @Autowired(
            required = false
    )
    private SwaggerGlobalOperationParameterConfig globalOperationParameter;

    public SwaggerConfiguration() {
    }

    @Bean
    public Docket createRestApi() {
        Docket docket = (new Docket(DocumentationType.SWAGGER_2)).apiInfo(this.apiInfo()).select().apis(RequestHandlerSelectors.basePackage(StringUtils.defaultString(this.swaggerProperties.getScan(), (String)SwaggerConstants.CONFIG_MAP.get("scan")))).paths(this.swaggerProperties.isEnable() ? PathSelectors.any() : PathSelectors.none()).build();
        if (this.globalOperationParameter != null) {
            docket.globalOperationParameters(this.globalOperationParameter.getGlobalOperationParameter());
        }

        docket.globalResponseMessage(RequestMethod.GET, this.getResponseMessage());
        docket.globalResponseMessage(RequestMethod.POST, this.getResponseMessage());
        docket.globalResponseMessage(RequestMethod.DELETE, this.getResponseMessage());
        docket.globalResponseMessage(RequestMethod.PUT, this.getResponseMessage());
        docket.globalResponseMessage(RequestMethod.OPTIONS, this.getResponseMessage());
        return docket;
    }

    private ApiInfo apiInfo() {
        return (new ApiInfoBuilder()).title(StringUtils.defaultString(this.swaggerProperties.getTitle(), (String)SwaggerConstants.CONFIG_MAP.get("title"))).description(StringUtils.defaultString(this.swaggerProperties.getDescription(), (String)SwaggerConstants.CONFIG_MAP.get("description"))).contact(this.swaggerProperties.getContact() == null ? new Contact((String)SwaggerConstants.CONFIG_MAP.get("contact.name"), (String)SwaggerConstants.CONFIG_MAP.get("contact.url"), (String)SwaggerConstants.CONFIG_MAP.get("contact.email")) : this.swaggerProperties.getContact()).version(StringUtils.defaultString(this.swaggerProperties.getVersion(), (String)SwaggerConstants.CONFIG_MAP.get("version"))).build();
    }

    private List<ResponseMessage> getResponseMessage() {
        return (List)Stream.of(CommonCode.values()).map((item) -> {
            return (new ResponseMessageBuilder()).code(item.code()).message(item.msg()).build();
        }).collect(Collectors.toList());
    }
}
