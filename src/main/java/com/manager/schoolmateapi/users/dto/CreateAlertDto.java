package com.manager.schoolmateapi.users.dto;
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
    private Double latitude;

    @NotNull
    private Double longitude;

}
