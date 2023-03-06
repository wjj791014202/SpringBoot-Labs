package com.validator;

import com.kucoin.common.validator.validator.EmailValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = EmailValidator.class
)
public @interface Email {
    boolean require() default true;

    String message() default "not validate email format!";
}
