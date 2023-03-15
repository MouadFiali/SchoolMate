package com.manager.schoolmateapi.documents.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.schoolmateapi.documents.models.Document;

public interface DocumentsRepository extends JpaRepository<Document, Long> {
}
