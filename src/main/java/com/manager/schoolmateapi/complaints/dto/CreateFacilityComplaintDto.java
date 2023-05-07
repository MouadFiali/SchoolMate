package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.complaints.enumerations.FacilityType;
import com.manager.schoolmateapi.complaints.validators.NotNullOnCondition;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NotNullOnCondition(className = "className", facilityType = "facilityType")
@EqualsAndHashCode(callSuper = true)
public class CreateFacilityComplaintDto extends CreateComplaintDto {
    @NotNull(message = "The facility type is required")
    private FacilityType facilityType;

    @Pattern(regexp = "[a-zA-Z]+[0-9]+$", 
    message = "The class name is invalid, please give the name of the class in the format 'Amphi1' or 'L9'")
    private String className;

}
