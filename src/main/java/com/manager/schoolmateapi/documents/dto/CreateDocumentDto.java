package com.manager.schoolmateapi.documents.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateDocumentDto {
  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Tags list is required")
  private List<Long> tags;

  @NotNull(message = "Shared is required")
  private Boolean shared;
}
