package com.validator;


import com.kucoin.common.validator.validator.NotNullValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = NotNullValidator.class
)
public @interface NotNull {
    String message() default "null is not allowed!";
}

