package com.i18n;

import com.kucoin.base.result.AbstractResult;
import com.kucoin.base.result.CommonCode;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class I18nMessageAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger logger = LoggerFactory.getLogger(I18nMessageAdvice.class);
    public static final String TRANSLATE_KEY = "translate";
    @Autowired
    private LocaleMessageResource localeMessageResource;

    public I18nMessageAdvice() {
    }

    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractResult.class.isAssignableFrom(returnType.getParameterType()) || ResponseEntity.class == returnType.getParameterType();
    }

    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body != null && AbstractResult.class.isAssignableFrom(body.getClass())) {
            this.handleAbstractResult((AbstractResult)body);
        }

        return body;
    }

    private void handleAbstractResult(AbstractResult body) {
        String message = body.getMsg();
        boolean translate = !Objects.equals(body.getCode(), String.valueOf(CommonCode.CODE_SUCCESS.code()));
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            Object value = requestAttributes.getAttribute("translate", 0);
            if (value != null) {
                translate = Objects.equals(value, Boolean.TRUE);
            }
        }

        if (translate && !StringUtils.containsWhitespace(message)) {
            try {
                body.setMsg(this.localeMessageResource.getText(message, body.getArgs()));
            } catch (Exception var6) {
                logger.warn("Failed to get locale message for text:" + message);
            }
        }

    }
}

