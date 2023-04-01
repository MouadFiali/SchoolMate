package com.manager.schoolmateapi.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.manager.schoolmateapi.users.dto.CreateUserDto;
import com.manager.schoolmateapi.users.dto.EditPasswordDto;
import com.manager.schoolmateapi.users.dto.EditUserDto;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;
import com.manager.schoolmateapi.users.services.UserService;
import com.manager.schoolmateapi.utils.MessageResponse;

import jakarta.validation.Valid;

@RestController
public class UserController {
	@Autowired
	UserService userService;

	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	User addUser(@Valid @RequestBody CreateUserDto createUserDto) {
		if(createUserDto.getRole()==null){
			createUserDto.setRole(UserRole.STUDENT);
		}
		return userService.addUser(createUserDto);
	}

	@GetMapping("/users/{id}")
	User getUser(@PathVariable("id") final Long id) {
		return userService.getUser(id);
	}

	@RequestMapping(value="/users", method = RequestMethod.GET)
	ResponseEntity<?> getUserByEmail(@RequestParam(value = "email", required = false) String email){
		if(email != null){
			User user = userService.getUserByEmail(email);
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.ok(userService.getAllUsers());
		}
	}

	@PatchMapping("/users/{id}")
	User editUser(
		@PathVariable("id") Long id,
		@Valid @RequestBody EditUserDto editUserDto){
			return userService.editUser(id, editUserDto);
	}

	@GetMapping("/me")
	User getPrincipal(){
		MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails.getUser();
	}

	@PatchMapping("/me/reset-password")
	MessageResponse editPassword(
		@Valid @RequestBody EditPasswordDto editPasswordDto){
			userService.editPassword(editPasswordDto);
			return new MessageResponse("Password changed successfully!");
	}

	@DeleteMapping("/users/{id}")
	MessageResponse deleteUser(@PathVariable("id") Long id){
		userService.deleteUser(id);
		return new MessageResponse("user deleted successfully");
	}
}
