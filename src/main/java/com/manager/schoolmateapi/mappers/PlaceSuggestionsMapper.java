package com.manager.schoolmateapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.geo.Point;

import com.manager.schoolmateapi.placesuggestions.PlaceSuggestions;
import com.manager.schoolmateapi.placesuggestions.dto.CreatePlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.dto.EditPlaceSuggestionDto;

@Mapper(componentModel = "spring")
public interface PlaceSuggestionsMapper {

    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "ListToPoint")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    PlaceSuggestions createDTOtoPlaceSuggestion(CreatePlaceSuggestionDto createPlaceSuggestionsDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "ListToPoint")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updatePlaceSuggestionFromDto(EditPlaceSuggestionDto editAlertDto, @MappingTarget PlaceSuggestions placeSuggestions);

    @Named("listToPoint")
    public static Point listToPoint(List<Double> coordinates) {
        return new Point(coordinates.get(0), coordinates.get(1));
    }
}