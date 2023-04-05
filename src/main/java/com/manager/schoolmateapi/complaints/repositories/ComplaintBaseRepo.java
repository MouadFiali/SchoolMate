package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.Complaint;

@Transactional
public interface ComplaintBaseRepo<T extends Complaint> extends JpaRepository<T, Long> {
    
}
