package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;

import jakarta.validation.constraints.NotBlank;

public class EditComplaintStatusDto {
    
    @NotBlank(message = "The status is required")
    private ComplaintStatus status;

}
