package com.validator;

import com.kucoin.common.validator.validator.NotEmptyValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = NotEmptyValidator.class
)
public @interface NotEmpty {
    String message() default "empty is not allowed!";
}