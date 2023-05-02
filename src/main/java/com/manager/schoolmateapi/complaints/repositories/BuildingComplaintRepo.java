package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.BuildingComplaint;

@Transactional
public interface BuildingComplaintRepo extends ComplaintBaseRepo<BuildingComplaint>{

    Iterable<BuildingComplaint> findAllByComplainantId(Long id);

    Page<BuildingComplaint> findAllByComplainantId(Long id, Pageable pageable);

}
