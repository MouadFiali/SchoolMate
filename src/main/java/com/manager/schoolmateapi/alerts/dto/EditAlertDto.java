package com.manager.schoolmateapi.alerts.dto;

import java.util.List;
import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.alerts.enumerations.AlertType;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class EditAlertDto {

    @Nullable
    private String title;

    @Nullable
    private String description;

    @Nullable
    private AlertType type;

    @Nullable
    private List<Double> coordinates;

    @Nullable
    private AlertStatus status;
}
