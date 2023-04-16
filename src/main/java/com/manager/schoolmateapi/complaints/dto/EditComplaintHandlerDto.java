package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.users.models.User;

import jakarta.validation.constraints.NotBlank;

public class EditComplaintHandlerDto {

    @NotBlank(message = "The complaint handler is required")
    private User handler;

}
