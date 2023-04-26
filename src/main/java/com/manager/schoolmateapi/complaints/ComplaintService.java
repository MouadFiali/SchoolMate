package com.manager.schoolmateapi.complaints;

import java.time.LocalDate;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;
import com.manager.schoolmateapi.complaints.enumerations.FacilityType;
import com.manager.schoolmateapi.complaints.models.BuildingComplaint;
import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;
import com.manager.schoolmateapi.complaints.models.RoomComplaint;
import com.manager.schoolmateapi.complaints.repositories.BuildingComplaintRepo;
import com.manager.schoolmateapi.complaints.repositories.ComplaintRepository;
import com.manager.schoolmateapi.complaints.repositories.FacilitiesComplaintRepo;
import com.manager.schoolmateapi.complaints.repositories.RoomComplaintRepo;
import com.manager.schoolmateapi.mappers.ComplaintDtoMapper;
import com.manager.schoolmateapi.users.models.User;

import lombok.Data;

@Data
@Service
public class ComplaintService {
    
  private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found");
  };

  @Autowired
  private BuildingComplaintRepo buildingComplaintRepo;

  @Autowired
  private RoomComplaintRepo roomComplaintRepo;

  @Autowired
  private FacilitiesComplaintRepo facilitiesComplaintRepo;

  @Autowired
  private ComplaintRepository complaintRepo;

  @Autowired
  private ComplaintDtoMapper complaintMapper;

  public RoomComplaint getRoomComplaint(final Long id) {
    return roomComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
  }

  public BuildingComplaint getBuildingComplaint(final Long id) {
    return buildingComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
  }

  public FacilitiesComplaint getFacilitiesComplaint(final Long id) {
    return facilitiesComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
  }

  public Iterable<RoomComplaint> getAllRoomComplaints() {
      return roomComplaintRepo.findAll();
  }

  public Iterable<RoomComplaint> getAllRoomComplaintsByUser(User user) {
    return roomComplaintRepo.findAllByComplainantId(user.getId());
  }

  public Iterable<BuildingComplaint> getAllBuildingComplaints() {
      return buildingComplaintRepo.findAll();
  }

  public Iterable<BuildingComplaint> getAllBuildingComplaintsByUser(User user) {
    return buildingComplaintRepo.findAllByComplainantId(user.getId());
  }

  public Iterable<FacilitiesComplaint> getAllFacilitiesComplaints() {
      return facilitiesComplaintRepo.findAll();
  }

  public Iterable<FacilitiesComplaint> getAllFacilitiesComplaintsByUser(User user) {
    return facilitiesComplaintRepo.findByComplainantId(user.getId());
  }

  public Iterable<Complaint> getAllComplaints() {
    return complaintRepo.findAll();
  }

  public Iterable<Complaint> getAllComplaintsByUser(User user) {
    return complaintRepo.findAllByComplainantId(user.getId());
  }

  public RoomComplaint addRoomComplaint(CreateRoomComplaintDto createRoomComplaintDto, User complainant){
    RoomComplaint roomComplaint = complaintMapper.createRoomComplaintDtoToRoomComplaint(createRoomComplaintDto);

    // Set the status to pending and the date to today
    roomComplaint.setStatus(ComplaintStatus.PENDING);
    roomComplaint.setDate(LocalDate.now());
    roomComplaint.setComplainant(complainant);

    return roomComplaintRepo.save(roomComplaint);
  }

  public BuildingComplaint addBuildingComplaint(CreateBuildingComplaintDto createBuildingComplaintDto, User complainant){
    BuildingComplaint buildingComplaint = complaintMapper.createBuildingComplaintDtoToBuildingComplaint(createBuildingComplaintDto);

    // Set the status to pending and the date to today
    buildingComplaint.setStatus(ComplaintStatus.PENDING);
    buildingComplaint.setDate(LocalDate.now());
    buildingComplaint.setComplainant(complainant);

    return buildingComplaintRepo.save(buildingComplaint);
  }

  public FacilitiesComplaint addFacilitiesComplaint(CreateFacilityComplaintDto createFacilityComplaintDto, User complainant){
    FacilitiesComplaint facilitiesComplaint = complaintMapper.createFacilityComplaintDtoToFacilityComplaint(createFacilityComplaintDto);

    // Set the status to pending and the date to today
    facilitiesComplaint.setStatus(ComplaintStatus.PENDING);
    facilitiesComplaint.setDate(LocalDate.now());
    facilitiesComplaint.setComplainant(complainant);

    return facilitiesComplaintRepo.save(facilitiesComplaint);
  }

  public RoomComplaint editRoomComplaint(CreateRoomComplaintDto createRoomComplaintDto, Long id){
    RoomComplaint newComplaint = complaintMapper.createRoomComplaintDtoToRoomComplaint(createRoomComplaintDto);
    // Get the existing complaint
    RoomComplaint oldComplaint = roomComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);

    oldComplaint.setDescription(newComplaint.getDescription());
    oldComplaint.setRoom(newComplaint.getRoom());
    oldComplaint.setRoomProb(newComplaint.getRoomProb());

    return roomComplaintRepo.save(oldComplaint);
  }

  public BuildingComplaint editBuildingComplaint(CreateBuildingComplaintDto createBuildingComplaintDto, Long id){
    BuildingComplaint newComplaint = complaintMapper.createBuildingComplaintDtoToBuildingComplaint(createBuildingComplaintDto);
    // Get the existing complaint
    BuildingComplaint oldComplaint = buildingComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);

    oldComplaint.setDescription(newComplaint.getDescription());
    oldComplaint.setBuilding(newComplaint.getBuilding());
    oldComplaint.setBuildingProb(newComplaint.getBuildingProb());

    return buildingComplaintRepo.save(oldComplaint);
  }

  public FacilitiesComplaint editFacilitiesComplaint(CreateFacilityComplaintDto createFacilityComplaintDto, Long id){
    FacilitiesComplaint newComplaint = complaintMapper.createFacilityComplaintDtoToFacilityComplaint(createFacilityComplaintDto);
    // Get the existing complaint
    FacilitiesComplaint oldComplaint = facilitiesComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);

    oldComplaint.setDescription(newComplaint.getDescription());
    oldComplaint.setFacilityType(newComplaint.getFacilityType());
    if(newComplaint.getFacilityType() == FacilityType.CLASS){
      oldComplaint.setClassName(newComplaint.getClassName());
    }

    return facilitiesComplaintRepo.save(oldComplaint);
  }

  public void deleteRoomComplaint(Long id){
    roomComplaintRepo.deleteById(id);
  }

  public void deleteBuildingComplaint(Long id){
    buildingComplaintRepo.deleteById(id);
  }

  public void deleteFacilitiesComplaint(Long id){
    facilitiesComplaintRepo.deleteById(id);
  }
    
}
