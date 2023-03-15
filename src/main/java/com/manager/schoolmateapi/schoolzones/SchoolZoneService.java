package com.manager.schoolmateapi.schoolzones;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.mappers.DtoMapper;
import com.manager.schoolmateapi.schoolzones.dto.CreateSchoolZoneDto;
import com.manager.schoolmateapi.schoolzones.dto.EditSchoolZoneDto;

@Service
public class SchoolZoneService {

  private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "School zone not found");
  };

  @Autowired
  SchoolZoneRepository schoolZoneRepository;

  @Autowired
  DtoMapper dtoMapper;

  public SchoolZone getSchoolZoneById(Long id) {
    return schoolZoneRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
  }

  public Iterable<SchoolZone> getAllSchoolZones() {
    return schoolZoneRepository.findAll();
  }

  public SchoolZone addSchoolZone(CreateSchoolZoneDto createSchoolZoneDto) {
    return schoolZoneRepository.save(dtoMapper.createDtoToSchoolZone(createSchoolZoneDto));
  }

  public SchoolZone editSchoolZone(Long id, EditSchoolZoneDto editSchoolZoneDto) {
    SchoolZone schoolZone = schoolZoneRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    dtoMapper.updateSchoolZoneFromDto(editSchoolZoneDto, schoolZone);
    schoolZoneRepository.save(schoolZone);
    return schoolZone;
  }

  public void deleteSchoolZone(Long id) {
    schoolZoneRepository.delete(schoolZoneRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER));
  }
}
