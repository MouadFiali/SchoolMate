package com.manager.schoolmateapi.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.users.dto.CreateUserDto;
import com.manager.schoolmateapi.users.dto.EditPasswordDto;
import com.manager.schoolmateapi.users.dto.EditUserDto;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;
import com.manager.schoolmateapi.users.services.UserService;
import com.manager.schoolmateapi.utils.MessageResponse;
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

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
	ResponseEntity<?> getUsers(@RequestParam(value = "email", required = false) String email, 
		Pageable pageable, @RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "role", required = false) UserRole role) {
		if(email != null){
			User user = userService.getUserByEmail(email);
			return ResponseEntity.ok(user);
		} else if(search != null){
			Page<User> results;
			if(role != null){
				results = userService.searchUsers(search, role, pageable);
			} else {
				results = userService.searchUsers(search, pageable);
			}
			PaginatedResponse<User> response = PaginatedResponse.<User>builder()
					.results(results.getContent())
					.page(results.getNumber())
					.totalPages(results.getTotalPages())
					.count(results.getNumberOfElements())
					.totalItems(results.getTotalElements())
					.last(results.isLast())
					.build();
			return ResponseEntity.ok(response);
		} else if(role != null){
			Page<User> results = userService.getUsersByRole(role, pageable);

			PaginatedResponse<User> response = PaginatedResponse.<User>builder()
					.results(results.getContent())
					.page(results.getNumber())
					.totalPages(results.getTotalPages())
					.count(results.getNumberOfElements())
					.totalItems(results.getTotalElements())
					.last(results.isLast())
					.build();

			return ResponseEntity.ok(response);

		} else {
			Page<User> results = userService.getAllUsers(pageable);

			PaginatedResponse<User> response = PaginatedResponse.<User>builder()
					.results(results.getContent())
					.page(results.getNumber())
					.totalPages(results.getTotalPages())
					.count(results.getNumberOfElements())
					.totalItems(results.getTotalElements())
					.last(results.isLast())
					.build();

			return ResponseEntity.ok(response);
		}
	}

	@PatchMapping("/users/{id}")
	User editUser(
		@PathVariable("id") Long id,
		@Valid @RequestBody EditUserDto editUserDto,
		@AuthenticationPrincipal MyUserDetails userDetails){
			// if the user is editing their own profile and they are not changing their role
			if(userDetails.getUser().getId() == id && editUserDto.getRole() == null){ 
				return userService.editUser(id, editUserDto);
			} else if (userDetails.getUser().getRole() == UserRole.MODERATOR && editUserDto.getRole() != null){
				if(editUserDto.getEmail()!=null || editUserDto.getFirstName()!=null || editUserDto.getLastName()!=null){
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only change the role of the user");
				}
				return userService.editUser(id, editUserDto);
			} else {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this user");
			}
				
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
	@PreAuthorize("hasAuthority('MODERATOR')")
	MessageResponse deleteUser(@PathVariable("id") Long id){
		userService.deleteUser(id);
		return new MessageResponse("User deleted successfully");
	}
}
