package com.manager.schoolmateapi.users.models;

import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.users.enumerations.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	@JsonIgnore
	private String password;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private UserRole role;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@OneToMany(mappedBy = "complainant", fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<Complaint> complaints;

	@OneToMany(mappedBy = "handler", fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<Complaint> assignedComplaints;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@JsonIgnore
	@EqualsAndHashCode.Exclude
	private Set<Document> documents;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@JsonIgnore
	@EqualsAndHashCode.Exclude
	private Set<DocumentTag> tags;

	// Crypt password before saving
	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}

	// Get full name
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
}
