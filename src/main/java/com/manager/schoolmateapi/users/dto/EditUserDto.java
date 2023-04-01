package com.manager.schoolmateapi.users.dto;


import com.manager.schoolmateapi.users.enumerations.UserRole;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EditUserDto {
    @Nullable
    private String firstName;

    @Nullable
	private String lastName;

    @Nullable
    @Pattern(regexp = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@um5.ac.ma$",
            message = "The email is invalid")
	private String email;

    @Nullable
    private UserRole role;

    @Nullable
    private boolean isActive;
}
