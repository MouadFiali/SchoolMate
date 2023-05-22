package com.manager.schoolmateapi.users;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByEmail(String email);

	public long deleteByFirstName(String firstName);

	public Page<User> findAll(Pageable pageable);

	public Page<User> findAllByRole(UserRole role, Pageable pageable);

	// find by searching for a substring in the first name or last name or email while ignoring case
	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String lastName, String email, Pageable pageable);

	// same thing but with a role
	public Page<User> findByFirstNameContainingIgnoreCaseAndRoleOrLastNameContainingIgnoreCaseAndRoleOrEmailContainingIgnoreCaseAndRole(String firstName, UserRole role1, String lastName, UserRole role2, String email, UserRole role3, Pageable pageable);
}
