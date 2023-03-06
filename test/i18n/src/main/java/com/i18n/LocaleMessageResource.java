package com.i18n;


import java.util.Locale;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class LocaleMessageResource {
    @Autowired
    private MessageSource messageSource;

    public LocaleMessageResource() {
    }

    public String getTextIfPresent(String key) {
        try {
            return this.getText(key);
        } catch (Exception var3) {
            return key;
        }
    }

    public String getText(String key) {
        return this.getText(key, (Object[])null);
    }

    public String getText(String key, Object... objects) {
        return this.getText(key, this.getLocale(), objects);
    }

    public String getText(String key, Locale locale, Object... objects) {
        return this.messageSource.getMessage(key, objects, locale);
    }

    public Locale getLocale() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            String langStr = (String)Optional.ofNullable(request.getHeader("LANG")).orElse(request.getParameter("lang"));
            if (langStr != null && !langStr.isEmpty()) {
                return LocaleUtils.toLocale(langStr);
            }
        }

        return LocaleContextHolder.getLocale();
    }
}

