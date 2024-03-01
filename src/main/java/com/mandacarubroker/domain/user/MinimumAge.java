package com.mandacarubroker.domain.user;

import com.mandacarubroker.validation.MinimumAgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = MinimumAgeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumAge {
    String message() default "Minimum age must be {value} years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value();
}
