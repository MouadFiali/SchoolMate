package com.manager.schoolmateapi.documents.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.documents.models.DocumentTag;

public interface DocumentTagsRepository extends JpaRepository<DocumentTag, Long> {
}
