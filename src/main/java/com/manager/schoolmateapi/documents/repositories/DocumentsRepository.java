package com.manager.schoolmateapi.documents.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.users.models.User;

public interface DocumentsRepository extends JpaRepository<Document, Long> {
  public Optional<Document> findByIdAndUser(long id, User user);

  public Iterable<Document> findByUser(User user);

  public Page<Document> findByUser(User user, Pageable pageable);

  public Page<Document> findByUserAndTagsIn(User user, Iterable<DocumentTag> tags, Pageable pageable);

  public Page<Document> findByUserIdAndSharedTrue(long userId, Pageable pageable);
}
