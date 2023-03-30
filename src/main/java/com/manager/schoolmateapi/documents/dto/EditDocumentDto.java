package com.manager.schoolmateapi.documents.dto;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EditDocumentDto {
  @Nullable
  private String name;

  @Nullable
  private List<Long> tags;

  @Nullable
  private boolean shared;
}
