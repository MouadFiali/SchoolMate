package com.manager.schoolmateapi.placesuggestions.dto;

import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;

import io.micrometer.common.lang.Nullable;
import lombok.Data;

@Data
public class EditPlaceSuggestionDto {
    @Nullable
    private String  description;

    @Nullable
    private PlaceSuggestionType type;

    @Nullable
    private Double latitude;

    @Nullable
    private Double longitude;
}
