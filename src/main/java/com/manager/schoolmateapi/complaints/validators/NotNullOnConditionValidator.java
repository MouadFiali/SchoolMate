package com.manager.schoolmateapi.complaints.validators;

import org.springframework.beans.BeanWrapperImpl;

import com.manager.schoolmateapi.complaints.enumerations.FacilityType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullOnConditionValidator implements ConstraintValidator<NotNullOnCondition, Object> {

    private String classNameField;
    private String facilityTypeField;

    @Override
    public void initialize(NotNullOnCondition constraintAnnotation) {
        classNameField = constraintAnnotation.className();
        facilityTypeField = constraintAnnotation.facilityType();
    }


    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);

        String className = (String) wrapper.getPropertyValue(this.classNameField);
        FacilityType facilityType = (FacilityType) wrapper.getPropertyValue(this.facilityTypeField);
        
        boolean isValid = true;
        if (facilityType!=null && (facilityType.equals(FacilityType.CLASS) && (className == null || className.isEmpty()))) {
            isValid = false;
        }
        context.disableDefaultConstraintViolation(); // Disable the default validation message
        context.buildConstraintViolationWithTemplate("The class name should not be empty if the facility type is a class") // Add a custom message
                .addPropertyNode(facilityTypeField) // Add the invalid field
                .addConstraintViolation(); // Add the message to the context
        return isValid;
    }
    
}
