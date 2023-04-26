package com.manager.schoolmateapi.complaints.repositories;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;


@Transactional
public interface FacilitiesComplaintRepo extends ComplaintBaseRepo<FacilitiesComplaint>{

    List<FacilitiesComplaint> findByComplainantId(Long id);
    
}
