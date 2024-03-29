package com.manager.schoolmateapi.users.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VerifyOldPasswordValidator.class)
public @interface VerifyOldPassword {
    String message() default "Incorrect current password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
