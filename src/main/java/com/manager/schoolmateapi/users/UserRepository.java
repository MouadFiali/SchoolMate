package com.manager.schoolmateapi.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.users.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByEmail(String email);
	public long DeleteByFirstName(String firstName);

}
