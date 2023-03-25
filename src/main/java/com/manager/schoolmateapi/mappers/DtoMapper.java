package com.manager.schoolmateapi.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

import com.manager.schoolmateapi.schoolzones.SchoolZone;
import com.manager.schoolmateapi.schoolzones.dto.CreateSchoolZoneDto;
import com.manager.schoolmateapi.schoolzones.dto.EditSchoolZoneDto;

@Mapper(componentModel = "spring")
public interface DtoMapper {

  @Mapping(source = "geometry", target = "geometry", qualifiedByName = "listToPolygon")
  @Mapping(target = "id", ignore = true)
  SchoolZone createDtoToSchoolZone(CreateSchoolZoneDto createSchoolZoneDto);
  
  
  
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "geometry", target = "geometry", qualifiedByName = "listToPolygon")
  @Mapping(target = "id", ignore = true)
  void updateSchoolZoneFromDto(EditSchoolZoneDto editSchoolZoneDto, @MappingTarget SchoolZone schoolZone);
 
  @Named("listToPolygon")
  public static Polygon listToPolygon(List<List<Double>> geometry) {
    List<Point> points = geometry.stream().map(coords -> {
      return new Point(coords.get(0), coords.get(1));
    }).collect(Collectors.toList());
    return new Polygon(points);
  }
  
  
}
