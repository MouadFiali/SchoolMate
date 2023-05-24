package com.manager.schoolmateapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.geo.Point;

import com.manager.schoolmateapi.alerts.Alert;
import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface AMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "listToPoint")
    Alert createDtoToAlert(CreateAlertDto createAlertDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(source = "coordinates", target = "coordinates")
    void updateAlertFromDto(EditAlertDto editAlertDto, @MappingTarget Alert alert);

    @Named("listToPoint")
    public static List<Double> listToPoint(List<Double> coordinates) {
        return coordinates;
    }
}
