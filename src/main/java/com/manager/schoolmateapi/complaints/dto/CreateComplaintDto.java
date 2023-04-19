package com.manager.schoolmateapi.complaints.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateComplaintDto {
    
    @NotBlank(message = "The description is required")
    protected String description;
    
}
