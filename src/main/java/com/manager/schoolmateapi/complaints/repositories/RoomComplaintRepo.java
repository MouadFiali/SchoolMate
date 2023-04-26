package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.RoomComplaint;

@Transactional
public interface RoomComplaintRepo extends ComplaintBaseRepo<RoomComplaint>{

    Iterable<RoomComplaint> findAllByComplainantId(Long id);
    
}
