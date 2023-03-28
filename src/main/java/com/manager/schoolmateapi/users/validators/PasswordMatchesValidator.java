package com.manager.schoolmateapi.users.validators;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private String passwordFieldName;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        passwordFieldName = constraintAnnotation.passwordField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        String password = (String) beanWrapper.getPropertyValue(passwordFieldName);
        String confirmPassword = (String) value;
        if (password != null && password.equals(confirmPassword)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(passwordFieldName)
                .addConstraintViolation();
        return false;
    }
}
