package com.manager.schoolmateapi.placesuggestions;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.users.models.User;

public interface PlaceSuggestionRepository extends JpaRepository<PlaceSuggestions, Long> {

    public Page<PlaceSuggestions> findByUser (User user, Pageable pageable);

    public Page<PlaceSuggestions> findByUserId(Long id, Pageable pageable);

    public Optional<PlaceSuggestions> findById(Long id);

}
