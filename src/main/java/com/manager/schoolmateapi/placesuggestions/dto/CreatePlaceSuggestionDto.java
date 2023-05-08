package com.manager.schoolmateapi.placesuggestions.dto;

import java.util.List;

import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;

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
public class CreatePlaceSuggestionDto {

    @NotBlank(message = "A description is required")
    private String description;

    @NotNull(message = "The category is necessary")
    private PlaceSuggestionType suggestiontype;

    @NotNull(message = "The coordinates are required")
    private List<Double> coordinates;
}