package com.manager.schoolmateapi.alerts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.users.models.User;


public interface AlertRepository extends JpaRepository<Alert, Long> {

    public Optional<Alert> findByIdAndUser(long id, User user);
    public Iterable<Alert> findByUser(User user);
}