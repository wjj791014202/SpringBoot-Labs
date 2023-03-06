package com.log;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LogAnnotation {
    String digestLogType() default "";

    String detailLogType() default "";

    short keyArg() default -1;

    String keyProps() default "";

    String name() default "";

    String includeArgs() default "";

    String excludeArgs() default "";
}

