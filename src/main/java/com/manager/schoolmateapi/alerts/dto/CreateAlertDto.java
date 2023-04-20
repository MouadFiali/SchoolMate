package com.manager.schoolmateapi.alerts.dto;
import java.util.List;

import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.alerts.enumerations.AlertType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAlertDto {
    @NotBlank(message = "The title is required")
    private String title;
    @NotBlank(message = "The description is required")
    private String description;

    @NotNull(message = "The type is necessary")
    private AlertType type;

    @NotNull(message = "The coordinates are necessary")
    private List<Double> coordinates;

    @NotNull(message = "The status is necessary")
    private AlertStatus status;

}