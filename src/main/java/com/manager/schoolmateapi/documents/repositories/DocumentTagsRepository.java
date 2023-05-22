package com.manager.schoolmateapi.documents.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.users.models.User;

public interface DocumentTagsRepository extends JpaRepository<DocumentTag, Long> {
  public Iterable<DocumentTag> findByUser(User user);

  public Optional<DocumentTag> findByIdAndUser(long id, User user);

  public Iterable<DocumentTag> findAllByIdInAndUser(Iterable<Long> ids, User user);
}
