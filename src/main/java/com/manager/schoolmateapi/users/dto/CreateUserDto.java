package com.manager.schoolmateapi.users.dto;

import com.manager.schoolmateapi.users.validators.PasswordMatches;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserDto {
    
    @NotBlank(message = "The first name is required")
    private String firstName;

    @NotBlank(message = "The last name is required")
	private String lastName;

    @NotBlank(message = "The email is required")
    @Pattern(regexp = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@um5.ac.ma$",
            message = "The email is invalid")
	private String email;

    @NotBlank(message = "The password is required")
    @Pattern(regexp = "^((?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])){8,100}$",
            message = "The password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit and must be at least 8 characters")
	private String password;

    @PasswordMatches(passwordField = "password")
    private String confirmPassowrd;
}
