package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.complaints.enumerations.FacilityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateFacilityComplaintDto extends CreateComplaintDto {

    @NotBlank(message = "The facility type is required")
    private FacilityType facilityType;

    @Pattern(regexp = "[a-zA-Z]+[1-9]+$", 
    message = "The class name is invalid, please give the name of the class in the format 'Amphi1' or 'L9'") 
    private String className;
}
