package com.manager.schoolmateapi.alerts;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.users.models.User;


public interface AlertRepository extends JpaRepository<Alert, Long> {

    public Page<Alert> findByUser(User user, Pageable pageable);
    public Optional<Alert> findByIdAndUser(long id, User user);
    public Iterable<Alert> findByUser(User user);
    public Page<Alert> findByStatus(AlertStatus status, Pageable pageable);
}