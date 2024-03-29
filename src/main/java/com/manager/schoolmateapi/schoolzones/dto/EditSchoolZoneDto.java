package com.manager.schoolmateapi.schoolzones.dto;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditSchoolZoneDto {
  @Nullable
  @Size(max = 100, message = "The name should not exceed 100 characters")
  private String name;

  @Nullable
  private String description;

  @Nullable
  @Size(min = 3, max = 50, message = "Geometry must be a list of 3 to 50 points (lists of 2)")
  private List<List<Double>> geometry;
}
