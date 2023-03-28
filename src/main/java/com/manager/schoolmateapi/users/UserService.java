package com.manager.schoolmateapi.users;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

	public User getUser(final Long id) {
		return userRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
	}

	public User getUserByEmail(final String email) {
		return userRepository.findByEmail(email).orElseThrow(NOT_FOUND_HANDLER);
	}

	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User saveUser(User user) {
		User savedUser = userRepository.save(user);
		return savedUser;
	}
}
