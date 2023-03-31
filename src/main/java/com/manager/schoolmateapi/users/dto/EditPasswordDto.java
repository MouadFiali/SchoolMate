package com.manager.schoolmateapi.users.dto;

import com.manager.schoolmateapi.users.validators.PasswordMatches;
import com.manager.schoolmateapi.users.validators.VerifyOldPassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EditPasswordDto {
    
    @NotBlank
    @VerifyOldPassword
    private String oldPassword;

    @NotBlank(message = "The password is required")
    @Pattern(regexp = "^((?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])){8,100}$",
            message = "The password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit and must be at least 8 characters")
	private String password;

    @PasswordMatches(passwordField = "password")
    private String confirmPassowrd;
}
