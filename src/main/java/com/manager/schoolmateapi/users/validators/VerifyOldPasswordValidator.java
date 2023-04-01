package com.manager.schoolmateapi.users.validators;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.manager.schoolmateapi.users.models.MyUserDetails;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VerifyOldPasswordValidator implements ConstraintValidator<VerifyOldPassword, String> {

    @Override
    public void initialize(VerifyOldPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String password = userDetails.getPassword();
        return BCrypt.checkpw(value, password);
    }
}
