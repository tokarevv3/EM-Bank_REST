package com.example.bankcards.util.validation.annotation;

import com.example.bankcards.util.validation.validator.AllowedStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedStatusValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedStatus {
    String message() default "Недопустимый статус";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

