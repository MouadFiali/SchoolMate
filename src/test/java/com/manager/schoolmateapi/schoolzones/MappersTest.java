package com.manager.schoolmateapi.schoolzones;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.Matchers;

import com.manager.schoolmateapi.mappers.SchoolZoneMapper;
import com.manager.schoolmateapi.schoolzones.dto.CreateSchoolZoneDto;
import com.manager.schoolmateapi.schoolzones.dto.EditSchoolZoneDto;

@SpringBootTest
public class MappersTest {

  @Autowired
  SchoolZoneMapper schoolZoneMapper;

  @Test
  public void testCreateSchoolZoneFromDto_shouldReturnSchoolZoneSuccessfully() {
    CreateSchoolZoneDto createSchoolZoneDto = CreateSchoolZoneDto
        .builder()
        .name("Buvette")
        .description("Place where you can grab some snacks")
        .geometry(SchoolZoneControllerTest.generateRandomGeometry())
        .build(); // Tags cannot be tested since they need the data source

    SchoolZone schoolZone = schoolZoneMapper.createDtoToSchoolZone(createSchoolZoneDto);
    assertThat(schoolZone.getName(), Matchers.is(createSchoolZoneDto.getName()));
    assertThat(schoolZone.getDescription(), Matchers.is(createSchoolZoneDto.getDescription()));
    assertThat(schoolZone.getGeometry().getPoints().size(), Matchers.is(createSchoolZoneDto.getGeometry().size()));
  }

  @Test
  public void testEditSchoolZoneFromDto_shouldReturnChangedSchoolZone() {
    EditSchoolZoneDto editSchoolZoneDto = EditSchoolZoneDto
        .builder()
        .name("Buvette")
        .description("Place where you can grab some snacks")
        .geometry(SchoolZoneControllerTest.generateRandomGeometry())
        .build();

    SchoolZone schoolZone = SchoolZone
        .builder()
        .name("Buvette N°1")
        .description("Place where you can grab some food")
        .geometry(SchoolZoneControllerTest.generateRandomPolygon())
        .build();

    schoolZoneMapper.updateSchoolZoneFromDto(editSchoolZoneDto, schoolZone);

    assertThat(schoolZone.getName(), Matchers.is(editSchoolZoneDto.getName()));
    assertThat(schoolZone.getDescription(), Matchers.is(editSchoolZoneDto.getDescription()));
    assertThat(schoolZone.getGeometry().getPoints().size(), Matchers.is(editSchoolZoneDto.getGeometry().size()));
  }
}
