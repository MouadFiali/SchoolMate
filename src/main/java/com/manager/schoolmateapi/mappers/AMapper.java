package com.manager.schoolmateapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.geo.Point;


import com.manager.schoolmateapi.alerts.Alert;
import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;


@Mapper(componentModel = "spring")
public interface AMapper {

    @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "ListToPoint")
    @Mapping(target = "id", ignore = true)
    @Mapping(target="user", ignore= true)
    Alert createDtoToAlert(CreateAlertDto createAlertDto);

     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     @Mapping(source = "coordinates", target = "coordinates", qualifiedByName = "ListToPoint")
     @Mapping(target = "id", ignore = true)
     @Mapping(target="user", ignore= true)
    void updateAlertFromDto(EditAlertDto editAlertDto, @MappingTarget Alert alert);

    @Named("listToPoint")
    public static Point listToPoint(List<Double> coordinates) {
        return new Point(coordinates.get(0), coordinates.get(1));
    }
}
