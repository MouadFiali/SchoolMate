package com.manager.schoolmateapi.documents.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditDocumentTagDto {
  @Nullable
  @Size(min = 1, max = 50)
  public String name;
}
