package com.manager.schoolmateapi.complaints.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateComplaintDto {
    
    @NotBlank(message = "The description is required")
    private String description;
    
}
