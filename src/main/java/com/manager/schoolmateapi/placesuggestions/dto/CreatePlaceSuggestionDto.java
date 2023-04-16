package com.manager.schoolmateapi.placesuggestions.dto;

import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePlaceSuggestionDto {

    @NotBlank(message = "A description is required")
    private String description;

    @NotNull(message = "The category is necessary")
    private PlaceSuggestionType type;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}