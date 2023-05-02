package com.manager.schoolmateapi.complaints.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;


@Transactional
public interface FacilitiesComplaintRepo extends ComplaintBaseRepo<FacilitiesComplaint>{

    List<FacilitiesComplaint> findByComplainantId(Long id);

    Page<FacilitiesComplaint> findAllByComplainantId(Long id, Pageable pageable);
    
}
