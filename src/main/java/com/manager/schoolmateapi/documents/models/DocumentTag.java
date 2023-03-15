package com.manager.schoolmateapi.documents.models;

import java.util.Date;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "document_tags")
public class DocumentTag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @CreatedDate
  @Column(nullable = false)
  private Date createdAt;

  @ManyToMany(mappedBy = "tags")
  private Set<Document> documents;
}
