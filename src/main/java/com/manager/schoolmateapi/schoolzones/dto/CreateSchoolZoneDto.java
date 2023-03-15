package com.manager.schoolmateapi.schoolzones.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSchoolZoneDto {
  @NotBlank(message = "The name is required")
  @Size(max = 100, message = "The name should not exceed 100 characters")
  private String name;

  @NotBlank(message = "The description is required")
  private String description;

  @NotNull(message = "The geometry is required")
  @Size(min = 3, max = 50, message = "Geometry must be a list of 3 to 50 points (lists of 2)")
  private List<List<Double>> geometry;
}
