package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;


@Transactional
public interface FacilitiesComplaintRepo extends ComplaintBaseRepo<FacilitiesComplaint>{
    
}
