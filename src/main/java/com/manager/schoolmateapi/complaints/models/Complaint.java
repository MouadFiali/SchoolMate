package com.manager.schoolmateapi.complaints.models;

import java.util.Date;

import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;
import com.manager.schoolmateapi.users.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Inheritance
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "complaints")
public class Complaint {
    @Id
	@Column(name = "complaint_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private ComplaintStatus status;

    @Column(name = "date", nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User complainant;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User handler;

}
