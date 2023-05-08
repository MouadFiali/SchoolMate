package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;
import com.manager.schoolmateapi.complaints.models.RoomComplaint;

@Transactional
public interface RoomComplaintRepo extends ComplaintBaseRepo<RoomComplaint>{

    Iterable<RoomComplaint> findAllByComplainantId(Long id);

    Page<RoomComplaint> findAllByComplainantId(Long id, Pageable pageable);
    
    Page<RoomComplaint> findAllByStatus(ComplaintStatus status, Pageable pageable);

    Page<RoomComplaint> findAllByStatusAndHandlerId(ComplaintStatus status, Long id, Pageable pageable);

    Page<RoomComplaint> findAllByHandlerId(Long id, Pageable pageable);
}
