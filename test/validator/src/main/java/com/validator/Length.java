package com.validator;

import com.kucoin.common.validator.validator.LengthValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = LengthValidator.class
)
public @interface Length {
    int min() default 0;

    int max() default 2147483647;

    boolean require() default true;

    String message() default "length range is not valid!";
}
