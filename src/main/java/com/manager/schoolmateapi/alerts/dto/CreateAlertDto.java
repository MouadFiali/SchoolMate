package com.manager.schoolmateapi.alerts.dto;
import java.util.List;


import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.alerts.enumerations.AlertType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAlertDto {
    @NotBlank(message = "The title is required")
    private String title;
    @NotBlank(message = "The description is required")
    private String description;

    @NotNull(message = "The type is necessary")
    private AlertType type;

    @NotNull(message = "The coordinates are necessary")
    private List<Double> coordinates;


}
