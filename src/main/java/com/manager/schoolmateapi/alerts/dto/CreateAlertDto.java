package com.manager.schoolmateapi.alerts.dto;
import org.springframework.data.geo.Point;

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

    @NotNull
    private Point coordinates;
}
