package com.manager.schoolmateapi.documents.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.users.models.User;

public interface DocumentsRepository extends JpaRepository<Document, Long> {
  public Optional<Document> findByIdAndUser(Long id, User user);

  public Iterable<Document> findByUser(User user);
}
