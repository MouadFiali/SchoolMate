package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.complaints.enumerations.BuildingProb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateBuildingComplaintDto extends CreateComplaintDto {

    @NotBlank(message = "The building reference is required")
    @Pattern(regexp = "[a-zA-Z]$", 
    message = "The building reference is invalid, please give the reference of the building in the format 'A' or 'B'")
    private String building;

    @NotBlank(message = "The type of the problem is required")
    private BuildingProb buildingProb;

}
