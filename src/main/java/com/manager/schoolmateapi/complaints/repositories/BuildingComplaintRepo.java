package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.BuildingComplaint;

@Transactional
public interface BuildingComplaintRepo extends ComplaintBaseRepo<BuildingComplaint>{

    Iterable<BuildingComplaint> findAllByComplainantId(Long id);
    
}
