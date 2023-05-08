package com.manager.schoolmateapi.complaints.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;


@Transactional
public interface ComplaintRepository extends ComplaintBaseRepo<Complaint> {

    Iterable<Complaint> findAllByComplainantId(Long id);

    Page<Complaint> findAllByComplainantId(Long id, Pageable pageable);

    Page<Complaint> findAllByStatus(ComplaintStatus status, Pageable pageable);

    Page<Complaint> findAllByStatusAndHandlerId(ComplaintStatus status, Long id, Pageable pageable);

    Page<Complaint> findAllByHandlerId(Long id, Pageable pageable);

}
