package com.manager.schoolmateapi.schoolzones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.manager.schoolmateapi.schoolzones.dto.CreateSchoolZoneDto;
import com.manager.schoolmateapi.schoolzones.dto.EditSchoolZoneDto;
import com.manager.schoolmateapi.utils.MessageResponse;

import jakarta.validation.Valid;

@RestController
public class SchoolZoneController {
  @Autowired
  SchoolZoneService schoolZoneService;

  @GetMapping("/school-zones")
  Iterable<SchoolZone> getAllSchoolZones() {
    return schoolZoneService.getAllSchoolZones();
  }

  @PostMapping("/school-zones")
  @ResponseStatus(HttpStatus.CREATED)
  SchoolZone addSchoolZone(@Valid @RequestBody CreateSchoolZoneDto createSchoolZoneDto) {
    return schoolZoneService.addSchoolZone(createSchoolZoneDto);
  }

  @GetMapping("/school-zones/{id}")
  SchoolZone getSchoolZone(@PathVariable("id") Long id) {
    return schoolZoneService.getSchoolZoneById(id);
  }

  @PatchMapping("/school-zones/{id}")
  SchoolZone updateSchoolZone(
      @PathVariable("id") Long id,
      @Valid @RequestBody EditSchoolZoneDto editSchoolZoneDto) {
    return schoolZoneService.editSchoolZone(id, editSchoolZoneDto);
  }

  @DeleteMapping("/school-zones/{id}")
  MessageResponse deleteSchoolZone(@PathVariable("id") Long id) {
    schoolZoneService.deleteSchoolZone(id);
    return new MessageResponse("School zone deleted successfully");
  }
}
