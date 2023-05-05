package com.manager.schoolmateapi.schoolzones;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.manager.schoolmateapi.SchoolMateApiApplication;
import com.manager.schoolmateapi.schoolzones.dto.CreateSchoolZoneDto;
import com.manager.schoolmateapi.schoolzones.dto.EditSchoolZoneDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SchoolMateApiApplication.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class SchoolZoneControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  SchoolZoneRepository schoolZoneRepository;

  static List<Double> generateRandomCoordinates() {
    return List.of(Math.random() * 180 - 90, Math.random() * 360 - 180);
  }

  static List<List<Double>> generateRandomGeometry() {
    List<List<Double>> geometry = new ArrayList<>();
    for (int i = 0; i < ThreadLocalRandom.current().nextInt(3, 51); i++) {
      geometry.add(generateRandomCoordinates());
    }
    return geometry;
  }

  static Polygon generateRandomPolygon() {
    List<List<Double>> coordinates = generateRandomGeometry();
    return new Polygon(coordinates.stream().map(coords -> {
      return new Point(coords.get(0), coords.get(1));
    }).collect(Collectors.toList()));
  }

  @BeforeAll
  public void setupAll() {
    objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);

    Configuration.setDefaults(new Configuration.Defaults() {
      private final JsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);
      private final MappingProvider mappingProvider = new JacksonMappingProvider(objectMapper);

      @Override
      public JsonProvider jsonProvider() {
        return jsonProvider;
      }

      @Override
      public MappingProvider mappingProvider() {
        return mappingProvider;
      }

      @Override
      public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
      }
    });
  }

  @BeforeEach
  public void setup() {
    schoolZoneRepository.deleteAll();
  }

  @Test
  public void testListSchoolZone_shouldReturnSchoolZonesList() throws Exception {
    int size = 50;
    for (int i = 0; i < size; i++) {
      SchoolZone schoolZone = SchoolZone
          .builder()
          .name("School Zone #" + i)
          .description("Description #" + i)
          .geometry(generateRandomPolygon())
          .build();
      schoolZoneRepository.save(schoolZone);
    }

    List<SchoolZone> schoolZones = schoolZoneRepository.findAll();
    List<Integer> geometriesSizes = schoolZones.stream().map(schoolZone -> {
      return schoolZone.getGeometry().getPoints().size();
    }).collect(Collectors.toList());

    mockMvc
        .perform(get("/school-zones").with(user("user")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", Matchers.is(size)))
        .andExpect(jsonPath("$[*].id",
            Matchers.containsInAnyOrder(
                schoolZones.stream().map(zone -> zone.getId()).toArray())))
        .andExpect(
            jsonPath("$[*].name", Matchers.containsInAnyOrder(
                schoolZones.stream().map(schoolZone -> schoolZone.getName())
                    .toArray())))
        .andExpect(
            jsonPath("$[*].description", Matchers.containsInAnyOrder(
                schoolZones.stream().map(schoolZone -> schoolZone.getDescription())
                    .toArray())))
        .andExpect(
            jsonPath("$[*].geometry.points.length()", Matchers.containsInAnyOrder(geometriesSizes.toArray())));

  }

  @Test
  public void testGetSchoolZone_shouldReturnSchoolZone() throws Exception {
    SchoolZone schoolZone = SchoolZone
        .builder()
        .name("Block A")
        .description("Boys Living Place")
        .geometry(generateRandomPolygon())
        .build();
    schoolZoneRepository.save(schoolZone);

    mockMvc
        .perform(get("/school-zones/" + schoolZone.getId()).with(user("user")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(schoolZone.getId())))
        .andExpect(jsonPath("$.name", Matchers.is(schoolZone.getName())))
        .andExpect(jsonPath("$.description", Matchers.is(schoolZone.getDescription())))
        .andExpect(jsonPath("$.geometry.points.length()", Matchers.is(schoolZone.getGeometry().getPoints().size())));
  }

  @Test
  public void testGetSchoolZone_givenWrongId_shouldReturnNotFound() throws Exception {
    SchoolZone schoolZone = SchoolZone
        .builder()
        .name("Block C")
        .description("Boys Living Place")
        .geometry(generateRandomPolygon())
        .build();
    schoolZoneRepository.save(schoolZone);

    mockMvc
        .perform(get("/school-zones/1").with(user("user")))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testCreateSchoolZone_shouldReturnSchoolZone() throws Exception {
    CreateSchoolZoneDto schoolZoneDto = CreateSchoolZoneDto.builder()
        .name("Block B")
        .description("Boys Living Place")
        .geometry(generateRandomGeometry())
        .build();

    mockMvc
        .perform(post("/school-zones").with(user("user"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(schoolZoneDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", Matchers.notNullValue()))
        .andExpect(jsonPath("$.name", Matchers.is(schoolZoneDto.getName())))
        .andExpect(jsonPath("$.description", Matchers.is(schoolZoneDto.getDescription())))
        .andExpect(jsonPath("$.geometry.points.length()", Matchers.is(schoolZoneDto.getGeometry().size())));
  }

  @Test
  public void testEditSchooZone_shouldReturnEditedSchoolZone() throws Exception {
    SchoolZone schoolZone = SchoolZone
        .builder()
        .name("Block D")
        .description("Boys Living Place")
        .geometry(generateRandomPolygon())
        .build();

    SchoolZone anotherSchoolZone = SchoolZone
        .builder()
        .name("Bloc A")
        .description("Boys Living Palace")
        .geometry(generateRandomPolygon())
        .build();

    schoolZoneRepository.saveAll(List.of(schoolZone, anotherSchoolZone));

    EditSchoolZoneDto editSchoolZoneDto = EditSchoolZoneDto.builder()
        .name("Block A")
        .description("Boys Living Place")
        .geometry(generateRandomGeometry())
        .build();

    mockMvc
        .perform(patch("/school-zones/" + anotherSchoolZone.getId()).with(user("user"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editSchoolZoneDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", Matchers.is(anotherSchoolZone.getId())))
        .andExpect(jsonPath("$.name", Matchers.is(editSchoolZoneDto.getName())))
        .andExpect(jsonPath("$.description", Matchers.is(editSchoolZoneDto.getDescription())))
        .andExpect(jsonPath("$.geometry.points.length()", Matchers.is(editSchoolZoneDto.getGeometry().size())));

    SchoolZone nonEditedSchoolZone = schoolZoneRepository.findById(schoolZone.getId()).orElseThrow();
    assertEquals(nonEditedSchoolZone.getName(), schoolZone.getName());
    assertEquals(nonEditedSchoolZone.getDescription(), schoolZone.getDescription());
    assertEquals(nonEditedSchoolZone.getGeometry().getPoints().size(), schoolZone.getGeometry().getPoints().size());
  }

  @Test
  public void testEditSchooZone_givenWrongId_shouldReturnEditedSchoolZone() throws Exception {
    SchoolZone schoolZone = SchoolZone
        .builder()
        .name("Bloc A")
        .description("Boys Living Palace")
        .geometry(generateRandomPolygon())
        .build();

    schoolZoneRepository.save(schoolZone);

    EditSchoolZoneDto editSchoolZoneDto = EditSchoolZoneDto.builder()
        .name("Block A")
        .description("Boys Living Place")
        .geometry(generateRandomGeometry())
        .build();

    mockMvc
        .perform(patch("/school-zones/" + schoolZone.getId() + 1).with(user("user"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editSchoolZoneDto)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    SchoolZone nonEditedSchoolZone = schoolZoneRepository.findById(schoolZone.getId()).orElseThrow();
    assertEquals(nonEditedSchoolZone.getName(), schoolZone.getName());
    assertEquals(nonEditedSchoolZone.getDescription(), schoolZone.getDescription());
    assertEquals(nonEditedSchoolZone.getGeometry().getPoints().size(), schoolZone.getGeometry().getPoints().size());
  }

  @Test
  public void testDeleteSchoolZone_shouldReturnSuccessMessage() throws Exception {
    SchoolZone sz = SchoolZone
        .builder()
        .name("Block D")
        .description("Boys Living Place")
        .geometry(generateRandomPolygon())
        .build();

    schoolZoneRepository.save(sz);

    mockMvc
        .perform(delete("/school-zones/" + sz.getId()).with(user("user")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").isString());

    assertFalse(schoolZoneRepository.existsById(sz.getId()));
  }

  @Test
  public void testDeleteSchoolZone_givenWrongId_shouldReturnNotFound() throws Exception {
    SchoolZone sz = SchoolZone
        .builder()
        .name("Block D")
        .description("Boys Living Place")
        .geometry(generateRandomPolygon())
        .build();

    schoolZoneRepository.save(sz);

    mockMvc
        .perform(delete("/school-zones/" + sz.getId() + 1).with(user("user")))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

    assertTrue(schoolZoneRepository.existsById(sz.getId()));
  }
}