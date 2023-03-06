package com.validator;


import com.kucoin.common.validator.validator.RangeValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = RangeValidator.class
)
public @interface Range {
    long min() default 0L;

    long max() default 9223372036854775807L;

    boolean require() default true;

    boolean includeMin() default true;

    boolean includeMax() default true;

    String message() default "not in range value!";
}

