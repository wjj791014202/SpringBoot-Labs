package com.validator;


import com.kucoin.common.validator.validator.MobileValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validator = MobileValidator.class
)
public @interface Mobile {
    boolean require() default true;

    String message() default "not valid mobile format!";
}
