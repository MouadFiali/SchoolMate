package com.manager.schoolmateapi.complaints;

import java.time.LocalDate;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusAndHandlerDto;
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

  // Get paginated room complaints
  public Page<RoomComplaint> getAllRoomComplaintsPaginated(Pageable pageable) {
    return roomComplaintRepo.findAll(pageable);
  }

  public Iterable<RoomComplaint> getAllRoomComplaintsByUser(Long id) {
    return roomComplaintRepo.findAllByComplainantId(id);
  }

  // Get paginated room complaints by user
  public Page<RoomComplaint> getAllRoomComplaintsByUserPaginated(Long id, Pageable pageable) {
    return roomComplaintRepo.findAllByComplainantId(id, pageable);
  }

  public Iterable<BuildingComplaint> getAllBuildingComplaints() {
      return buildingComplaintRepo.findAll();
  }

  // Get paginated building complaints
  public Page<BuildingComplaint> getAllBuildingComplaintsPaginated(Pageable pageable) {
    return buildingComplaintRepo.findAll(pageable);
  }

  public Iterable<BuildingComplaint> getAllBuildingComplaintsByUser(Long id) {
    return buildingComplaintRepo.findAllByComplainantId(id);
  }

  // Get paginated building complaints by user
  public Page<BuildingComplaint> getAllBuildingComplaintsByUserPaginated(Long id, Pageable pageable) {
    return buildingComplaintRepo.findAllByComplainantId(id, pageable);
  }

  public Iterable<FacilitiesComplaint> getAllFacilitiesComplaints() {
      return facilitiesComplaintRepo.findAll();
  }

  // Get paginated facilities complaints
  public Page<FacilitiesComplaint> getAllFacilitiesComplaintsPaginated(Pageable pageable) {
    return facilitiesComplaintRepo.findAll(pageable);
  }

  public Iterable<FacilitiesComplaint> getAllFacilitiesComplaintsByUser(Long id) {
    return facilitiesComplaintRepo.findByComplainantId(id);
  }

  // Get paginated facilities complaints by user
  public Page<FacilitiesComplaint> getAllFacilitiesComplaintsByUserPaginated(Long id, Pageable pageable) {
    return facilitiesComplaintRepo.findAllByComplainantId(id, pageable);
  }

  public Iterable<Complaint> getAllComplaints() {
    return complaintRepo.findAll();
  }

  // Get paginated complaints (all types)
  public Page<Complaint> getAllComplaintsPaginated(Pageable pageable) {
    return complaintRepo.findAll(pageable);
  }

  public Iterable<Complaint> getAllComplaintsByUser(Long id) {
    return complaintRepo.findAllByComplainantId(id);
  }

  // Get paginated complaints by user (all types)
  public Page<Complaint> getAllComplaintsByUserPaginated(Long id, Pageable pageable) {
    return complaintRepo.findAllByComplainantId(id, pageable);
  }

  public Complaint getComplaint(Long id){
    return complaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
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

  public RoomComplaint editRoomComplaintDetails(CreateRoomComplaintDto createRoomComplaintDto, Long id, User principal){
    // Get the existing complaint
    RoomComplaint oldComplaint = roomComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    if(!oldComplaint.getComplainant().getId().equals(principal.getId())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this complaint");
    }
    if(!oldComplaint.getStatus().equals(ComplaintStatus.PENDING)){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The complaint is already being processed");
    }

    RoomComplaint newComplaint = complaintMapper.createRoomComplaintDtoToRoomComplaint(createRoomComplaintDto);

    oldComplaint.setDescription(newComplaint.getDescription());
    oldComplaint.setRoom(newComplaint.getRoom());
    oldComplaint.setRoomProb(newComplaint.getRoomProb());

    return roomComplaintRepo.save(oldComplaint);
  }

  public BuildingComplaint editBuildingComplaintDetails(CreateBuildingComplaintDto createBuildingComplaintDto, Long id, User principal){
    // Get the existing complaint
    BuildingComplaint oldComplaint = buildingComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    if(!oldComplaint.getComplainant().getId().equals(principal.getId())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this complaint");
    }
    if(!oldComplaint.getStatus().equals(ComplaintStatus.PENDING)){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The complaint is already being processed");
    }

    BuildingComplaint newComplaint = complaintMapper.createBuildingComplaintDtoToBuildingComplaint(createBuildingComplaintDto);

    oldComplaint.setDescription(newComplaint.getDescription());
    oldComplaint.setBuilding(newComplaint.getBuilding());
    oldComplaint.setBuildingProb(newComplaint.getBuildingProb());

    return buildingComplaintRepo.save(oldComplaint);
  }

  public FacilitiesComplaint editFacilitiesComplaintDetails(CreateFacilityComplaintDto createFacilityComplaintDto, Long id, User principal){
    // Get the existing complaint
    FacilitiesComplaint oldComplaint = facilitiesComplaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    if(!oldComplaint.getComplainant().getId().equals(principal.getId())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this complaint");
    }
    if(!oldComplaint.getStatus().equals(ComplaintStatus.PENDING)){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The complaint is already being processed");
    }

    FacilitiesComplaint newComplaint = complaintMapper.createFacilityComplaintDtoToFacilityComplaint(createFacilityComplaintDto);

    oldComplaint.setDescription(newComplaint.getDescription());
    oldComplaint.setFacilityType(newComplaint.getFacilityType());
    if(newComplaint.getFacilityType() == FacilityType.CLASS){
      oldComplaint.setClassName(newComplaint.getClassName());
    }

    return facilitiesComplaintRepo.save(oldComplaint);
  }

  public Complaint editComplaintStatusAndHandler(EditComplaintStatusAndHandlerDto editComplaintStatusAndHandlerDto, Long id){
    Complaint complaint = complaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    if(editComplaintStatusAndHandlerDto.getStatus()!=null && editComplaintStatusAndHandlerDto.getHandlerId()!=null){
      return complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintStatusAndHandlerDto, complaint);
    }
    else if(editComplaintStatusAndHandlerDto.getStatus()!=null){
      if(complaint.getHandler()==null && !editComplaintStatusAndHandlerDto.getStatus().equals(ComplaintStatus.PENDING)){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint must be assigned to a handler before changing the status");
      } else {
        return complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintStatusAndHandlerDto, complaint);
      }
    }
    else if(editComplaintStatusAndHandlerDto.getHandlerId()!=null){
      if(complaint.getHandler()==null){
        editComplaintStatusAndHandlerDto.setStatus(ComplaintStatus.ASSIGNED);
      }
      return complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintStatusAndHandlerDto, complaint);
    }
    else{
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "In the request body, no status or handlerId was specified");
    }
  }

  public void deleteComplaint(Long id, User principal){
    Complaint complaint = complaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    if(!complaint.getComplainant().getId().equals(principal.getId())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this complaint");
    }
    complaintRepo.deleteById(id);
  }

}
