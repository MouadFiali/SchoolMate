package com.manager.schoolmateapi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manager.schoolmateapi.model.User;
import com.manager.schoolmateapi.repository.UserRepository;

import lombok.Data;

@Data
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	public Optional<User> getUser(final Long id) {
		return userRepository.findById(id);
	}
	
	public Optional<User> getUserByEmail(final String email) {
		return userRepository.findByEmail(email);
	}
	
	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public User saveUser(User user) {
		User savedUser = userRepository.save(user);
		return savedUser;
	}
}
