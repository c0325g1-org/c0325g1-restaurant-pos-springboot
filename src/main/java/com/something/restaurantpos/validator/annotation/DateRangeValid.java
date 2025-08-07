package com.something.restaurantpos.validator.annotation;

import com.something.restaurantpos.validator.DateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRangeValid {
    String message() default "Ngày hết hạn phải sau hoặc bằng ngày bắt đầu";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
