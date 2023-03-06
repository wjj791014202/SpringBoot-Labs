package com.validator;

import com.kucoin.common.validator.validator.MinValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = MinValidator.class
)
public @interface Min {
    long value() default 0L;

    boolean includeMin() default true;

    boolean require() default true;

    String message() default "must bigger than min value!";
}
