package com.validator;

import com.kucoin.common.validator.validator.EqualValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = EqualValidator.class
)
public @interface Equal {
    long value() default 0L;

    boolean require() default true;

    String message() default "not equal to value!";
}
