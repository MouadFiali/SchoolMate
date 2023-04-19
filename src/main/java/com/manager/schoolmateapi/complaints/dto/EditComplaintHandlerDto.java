package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.users.models.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditComplaintHandlerDto {

    @NotBlank(message = "The complaint handler is required")
    private User handler;

}
