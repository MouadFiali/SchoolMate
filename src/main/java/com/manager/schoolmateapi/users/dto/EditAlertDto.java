package com.manager.schoolmateapi.users.dto;
import com.manager.schoolmateapi.alerts.enumerations.AlertType;
import jakarta.annotation.Nullable;
import lombok.Data;


@Data
public class EditAlertDto {

    @Nullable
    private String title;

    @Nullable
    private String description;

    @Nullable
    private AlertType type;

    @Nullable
    private Double latitude;

    @Nullable
    private Double longitude;
}
