package com.manager.schoolmateapi.users.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.manager.schoolmateapi.users.models.MyUserDetails;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VerifyOldPasswordValidator implements ConstraintValidator<VerifyOldPassword, String> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void initialize(VerifyOldPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String password = userDetails.getPassword();
        return passwordEncoder.matches(value, password);
    }

}
