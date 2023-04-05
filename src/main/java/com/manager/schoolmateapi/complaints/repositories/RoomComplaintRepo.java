package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.Complaint;

@Transactional
public interface RoomComplaintRepo extends ComplaintBaseRepo<Complaint>{
    
}
