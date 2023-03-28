package com.manager.schoolmateapi.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.manager.schoolmateapi.users.models.User;

@RestController
public class UserController {
	@Autowired
	UserService userService;

	@PostMapping("/user")
	public User createUser(@RequestBody User user) {
		return userService.saveUser(user);
	}

	@GetMapping("/user/{id}")
	public User getUser(@PathVariable("id") final Long id) {
		User user = userService.getUser(id);
		return user;
		
	}

	@GetMapping("/users")
	public Iterable<User> getAllUsers() {
		System.out.println("Received a request");
		return userService.getAllUsers();
	}
}
