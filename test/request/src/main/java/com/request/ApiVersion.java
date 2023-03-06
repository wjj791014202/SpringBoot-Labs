package com.request;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CustomPathPrefix("/v")
public @interface ApiVersion {
    @AliasFor(
            annotation = CustomPathPrefix.class,
            attribute = "version"
    )
    int value();
}

