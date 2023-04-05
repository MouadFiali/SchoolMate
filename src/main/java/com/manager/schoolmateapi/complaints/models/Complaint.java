package com.manager.schoolmateapi.complaints.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;

@Inheritance
@Entity
@Table(name = "complaints")
public class Complaint {
    
}
