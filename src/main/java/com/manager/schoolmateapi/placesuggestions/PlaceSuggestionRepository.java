package com.manager.schoolmateapi.placesuggestions;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.users.models.User;

public interface PlaceSuggestionRepository extends JpaRepository<PlaceSuggestions, Long> {

    public Optional<PlaceSuggestions> findByIdAndUser(long id, User user);

    public Iterable<PlaceSuggestions> findByUser(User user);

}
