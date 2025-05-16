package com.xpto.controlefinanceiro.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerTypeValidator.class)
@Documented
public @interface ValidCustomer {
    String message() default "Invalid client data for the specified type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
