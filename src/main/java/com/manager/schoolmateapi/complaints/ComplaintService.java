package com.manager.schoolmateapi.complaints;

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
import com.manager.schoolmateapi.onesignal.OneSignalService;
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

  @Autowired
  private OneSignalService oneSignalService;

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

  // Get all complaints by status (paginated)
  public Page<Complaint> getAllComplaintsByStatusPaginated(ComplaintStatus status, Pageable pageable) {
    return complaintRepo.findAllByStatus(status, pageable);
  }

  // Get all complaints by status and user (paginated)
  public Page<Complaint> getAllComplaintsByStatusAndUserPaginated(ComplaintStatus status, Long id, Pageable pageable) {
    return complaintRepo.findAllByStatusAndHandlerId(status, id, pageable);
  }

  // Get all building complaints by status (paginated)
  public Page<BuildingComplaint> getAllBuildingComplaintsByStatusPaginated(ComplaintStatus status, Pageable pageable) {
    return buildingComplaintRepo.findAllByStatus(status, pageable);
  }

  // Get all building complaints by status and user (paginated)
  public Page<BuildingComplaint> getAllBuildingComplaintsByStatusAndUserPaginated(ComplaintStatus status, Long id, Pageable pageable) {
    return buildingComplaintRepo.findAllByStatusAndHandlerId(status, id, pageable);
  }

  // Get all room complaints by status (paginated)
  public Page<RoomComplaint> getAllRoomComplaintsByStatusPaginated(ComplaintStatus status, Pageable pageable) {
    return roomComplaintRepo.findAllByStatus(status, pageable);
  }

  // Get all room complaints by status and user (paginated)
  public Page<RoomComplaint> getAllRoomComplaintsByStatusAndUserPaginated(ComplaintStatus status, Long id, Pageable pageable) {
    return roomComplaintRepo.findAllByStatusAndHandlerId(status, id, pageable);
  }

  // Get all facilities complaints by status (paginated)
  public Page<FacilitiesComplaint> getAllFacilitiesComplaintsByStatusPaginated(ComplaintStatus status, Pageable pageable) {
    return facilitiesComplaintRepo.findAllByStatus(status, pageable);
  }

  // Get all facilities complaints by status and user (paginated)
  public Page<FacilitiesComplaint> getAllFacilitiesComplaintsByStatusAndUserPaginated(ComplaintStatus status, Long id, Pageable pageable) {
    return facilitiesComplaintRepo.findAllByStatusAndHandlerId(status, id, pageable);
  }

  // Get all complaints by handler id (paginated)
  public Page<Complaint> getAllComplaintsByHandlerIdPaginated(Long id, Pageable pageable) {
    return complaintRepo.findAllByHandlerId(id, pageable);
  }

  // Get all building complaints by handler id (paginated)
  public Page<BuildingComplaint> getAllBuildingComplaintsByHandlerIdPaginated(Long id, Pageable pageable) {
    return buildingComplaintRepo.findAllByHandlerId(id, pageable);
  }

  // Get all room complaints by handler id (paginated)
  public Page<RoomComplaint> getAllRoomComplaintsByHandlerIdPaginated(Long id, Pageable pageable) {
    return roomComplaintRepo.findAllByHandlerId(id, pageable);
  }

  // Get all facilities complaints by handler id (paginated)
  public Page<FacilitiesComplaint> getAllFacilitiesComplaintsByHandlerIdPaginated(Long id, Pageable pageable) {
    return facilitiesComplaintRepo.findAllByHandlerId(id, pageable);
  }

  public Complaint getComplaint(Long id){
    return complaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
  }

  public RoomComplaint addRoomComplaint(CreateRoomComplaintDto createRoomComplaintDto, User complainant){
    RoomComplaint roomComplaint = complaintMapper.createRoomComplaintDtoToRoomComplaint(createRoomComplaintDto);

    // Set the status to pending and the date to today
    roomComplaint.setStatus(ComplaintStatus.PENDING);
    roomComplaint.setComplainant(complainant);

    roomComplaint = roomComplaintRepo.save(roomComplaint);

    // Notify the ADEI members of the new complaint
    oneSignalService.notifyAdeiMembersAboutNewComplaint(roomComplaint.getId(), complainant.getFullName());

    return roomComplaint;
  }

  public BuildingComplaint addBuildingComplaint(CreateBuildingComplaintDto createBuildingComplaintDto, User complainant){
    BuildingComplaint buildingComplaint = complaintMapper.createBuildingComplaintDtoToBuildingComplaint(createBuildingComplaintDto);

    // Set the status to pending and the date to today
    buildingComplaint.setStatus(ComplaintStatus.PENDING);
    buildingComplaint.setComplainant(complainant);

    buildingComplaint = buildingComplaintRepo.save(buildingComplaint);

    // Notify the ADEI members of the new complaint
    oneSignalService.notifyAdeiMembersAboutNewComplaint(buildingComplaint.getId(), complainant.getFullName());

    return buildingComplaint;
  }

  public FacilitiesComplaint addFacilitiesComplaint(CreateFacilityComplaintDto createFacilityComplaintDto, User complainant){
    FacilitiesComplaint facilitiesComplaint = complaintMapper.createFacilityComplaintDtoToFacilityComplaint(createFacilityComplaintDto);

    // Set the status to pending and the date to today
    facilitiesComplaint.setStatus(ComplaintStatus.PENDING);
    facilitiesComplaint.setComplainant(complainant);

    facilitiesComplaint = facilitiesComplaintRepo.save(facilitiesComplaint);

    // Notify the ADEI members of the new complaint
    oneSignalService.notifyAdeiMembersAboutNewComplaint(facilitiesComplaint.getId(), complainant.getFullName());

    return facilitiesComplaint;
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

  public Complaint editComplaintStatusAndHandler(EditComplaintStatusAndHandlerDto editComplaintStatusAndHandlerDto, Long id, User principal){
    Complaint complaint = complaintRepo.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    if(editComplaintStatusAndHandlerDto.getStatus()!=null && editComplaintStatusAndHandlerDto.getHandlerId()!=null){
      complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintStatusAndHandlerDto, complaint);
      complaint = complaintRepo.save(complaint);

      // Notify the complainant that the status of their complaint has changed
      oneSignalService.notifyComplainantAboutComplaintStatusChange(complaint.getId(), 
            complaint.getTitle(), 
            complaint.getComplainant().getEmail(),
            complaint.getStatus());
      
      if(complaint.getHandler().getId() != principal.getId()){
        // Notify the handler that they have been assigned a complaint (if they are not the one who assigned it)
        oneSignalService.notifyHandlerAboutNewComplaintAssigned(complaint.getId(),
            complaint.getTitle(), 
            complaint.getHandler().getEmail(), 
            principal);
      }
      

      return complaint;
    }
    else if(editComplaintStatusAndHandlerDto.getStatus()!=null){
      if(complaint.getHandler()==null && !editComplaintStatusAndHandlerDto.getStatus().equals(ComplaintStatus.PENDING)){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint must be assigned to a handler before changing the status");
      } else {
        complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintStatusAndHandlerDto, complaint);
        complaint = complaintRepo.save(complaint);

        // Notify the complainant that the status of their complaint has changed
        oneSignalService.notifyComplainantAboutComplaintStatusChange(complaint.getId(), 
              complaint.getTitle(), 
              complaint.getComplainant().getEmail(),
              complaint.getStatus());

        return complaint;
      }
    }
    else if(editComplaintStatusAndHandlerDto.getHandlerId()!=null){
      boolean assigned = false;

      // If the complaint is being assigned to a handler, set the status to ASSIGNED
      if(complaint.getHandler()==null){ // Means that the complaint is pending
        editComplaintStatusAndHandlerDto.setStatus(ComplaintStatus.ASSIGNED);
        assigned = true;
      }
      
      complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintStatusAndHandlerDto, complaint);
      complaint = complaintRepo.save(complaint);

      if(assigned){
        // Notify the complainant that the status of their complaint has changed
        oneSignalService.notifyComplainantAboutComplaintStatusChange(complaint.getId(), 
              complaint.getTitle(), 
              complaint.getComplainant().getEmail(),
              complaint.getStatus());
      }
      if(complaint.getHandler().getId() != principal.getId()){
        // Notify the handler that they have been assigned a complaint (if they are not the one who assigned it)
        oneSignalService.notifyHandlerAboutNewComplaintAssigned(complaint.getId(),
            complaint.getTitle(), 
            complaint.getHandler().getEmail(), 
            principal);
      }

      return complaint;
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

    // Notify the handler (if any) that the complaint has been deleted
    if(complaint.getHandler()!=null){
      String title = complaint.getTitle();
      String handlerEmail = complaint.getHandler().getEmail();
      oneSignalService.notifyHandlerAboutComplaintDeleted(title, handlerEmail);
    }
  }

}
