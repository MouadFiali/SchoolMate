package com.manager.schoolmateapi.users.validators;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private String passwordFieldName;
    private String passwordConfirmationFieldName;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        passwordFieldName = constraintAnnotation.password();
        passwordConfirmationFieldName = constraintAnnotation.passwordConfirmation();
    }

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);

        String password = (String) wrapper.getPropertyValue(passwordFieldName);
        String confirm = (String) wrapper.getPropertyValue(passwordConfirmationFieldName);
        
        boolean isValid = true;
        if (password == null || confirm == null || !password.equals(confirm)) {
            isValid = false;
        }
        context.disableDefaultConstraintViolation(); // Disable the default validation message
        context.buildConstraintViolationWithTemplate("Passwords do not match") // Add a custom message
                .addPropertyNode(passwordConfirmationFieldName) // Add the invalid field
                .addConstraintViolation(); // Add the message to the context
        return isValid;
    }
}
