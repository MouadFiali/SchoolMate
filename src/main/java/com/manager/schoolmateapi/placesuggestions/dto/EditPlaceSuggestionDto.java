package com.manager.schoolmateapi.placesuggestions.dto;

import org.springframework.data.geo.Point;

import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;

import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditPlaceSuggestionDto {
    @Nullable
    private String  description;

    @Nullable
    private PlaceSuggestionType suggestiontype;

    @Nullable
    private Point coordinates;
}
