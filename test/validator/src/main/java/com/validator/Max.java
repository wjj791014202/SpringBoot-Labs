package com.validator;

import com.kucoin.common.validator.validator.MaxValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = MaxValidator.class
)
public @interface Max {
    long value() default 9223372036854775807L;

    boolean includeMax() default true;

    boolean require() default true;

    String message() default "must smaller than max value!";
}
