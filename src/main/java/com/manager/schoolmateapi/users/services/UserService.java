package com.manager.schoolmateapi.users.services;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.mappers.UserDtoMapper;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.dto.CreateUserDto;
import com.manager.schoolmateapi.users.dto.EditPasswordDto;
import com.manager.schoolmateapi.users.dto.EditUserDto;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;

import lombok.Data;

@Data
@Service
public class UserService {
	
	private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
	  };

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserDtoMapper userDtoMapper;

	public User getUser(final Long id) {
		return userRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
	}

	public User getUserByEmail(final String email) {
		return userRepository.findByEmail(email).orElseThrow(NOT_FOUND_HANDLER);
	}

	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Get all users paginated
	public Page<User> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	// Get users by search
	public Page<User> searchUsers(String search, Pageable pageable) {
		return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, search, pageable);
	}

	// Get users by search and role
	public Page<User> searchUsers(String search, UserRole role, Pageable pageable) {
		// The JPA query will need all the search parameters (for all fields) 
		// and also the role 3 times because each time it will need to compare it to a different field
		// But as we want to search by the same search term for all fields, we can just pass it 3 times
		// Same thing for the role
		return userRepository.findByFirstNameContainingIgnoreCaseAndRoleOrLastNameContainingIgnoreCaseAndRoleOrEmailContainingIgnoreCaseAndRole(search, role, search, role, search, role, pageable);
	}

	// Get users by role
	public Page<User> getUsersByRole(UserRole role, Pageable pageable) {
		return userRepository.findAllByRole(role, pageable);
	}


	public User addUser(CreateUserDto createUserDto){
		User user = userDtoMapper.createUserDtoToUser(createUserDto);
		user.setActive(true);
		return userRepository.save(user);
	}

	public User editUser(Long id, EditUserDto editUserZoneDto) {
		User user = userRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
		userDtoMapper.updateUserFromDto(editUserZoneDto, user);
		return userRepository.save(user);
	}

	public User editPassword(EditPasswordDto editPasswordDto){
		MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userDetails.getUser();
		userDtoMapper.updatePasswordFromDto(editPasswordDto, user);
		return userRepository.save(user);
	}

	public void deleteUser(Long id){
		userRepository.delete(userRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER));
	}
}
