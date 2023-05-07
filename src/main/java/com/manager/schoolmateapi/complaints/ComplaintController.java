package com.manager.schoolmateapi.complaints;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusAndHandlerDto;
import com.manager.schoolmateapi.complaints.models.BuildingComplaint;
import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;
import com.manager.schoolmateapi.complaints.models.RoomComplaint;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.utils.MessageResponse;
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

import jakarta.validation.Valid;

@RestController
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping("/complaints")
    @ResponseStatus(HttpStatus.CREATED)
    Complaint addComplaint(@Valid @RequestBody CreateComplaintDto createComplaintDto, @AuthenticationPrincipal MyUserDetails userDetails) {
        if(createComplaintDto instanceof CreateBuildingComplaintDto){
            BuildingComplaint complaint = complaintService.addBuildingComplaint((CreateBuildingComplaintDto) createComplaintDto, userDetails.getUser());
            complaint.setDtype("BuildingComplaint"); // TODO: find a better way to do this
            return complaint;
        } else if(createComplaintDto instanceof CreateRoomComplaintDto){
            RoomComplaint complaint = complaintService.addRoomComplaint((CreateRoomComplaintDto) createComplaintDto, userDetails.getUser());
            complaint.setDtype("RoomComplaint"); // set the discriminator value manually as it's not set yet by hibernate
            return complaintService.getComplaint(complaint.getId());
        } else if(createComplaintDto instanceof CreateFacilityComplaintDto){
            FacilitiesComplaint complaint = complaintService.addFacilitiesComplaint((CreateFacilityComplaintDto) createComplaintDto, userDetails.getUser());
            complaint.setDtype("FacilitiesComplaint"); // TODO: find a better way to do this
            return complaintService.getComplaint(complaint.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid complaint type");
        }
    }

    @GetMapping("/complaints")
    PaginatedResponse<?> getAllComplaints(
        @AuthenticationPrincipal MyUserDetails userDetails,
        Pageable pageable,
        @RequestParam(required = false) String type, 
        @RequestParam(required = false) String user){
        //All compaints
        if(type == null || type.equals("all")){
            // for all users
            if(user == null || user.equals("all")){
                Page<Complaint> results = complaintService.getAllComplaintsPaginated(pageable);
                PaginatedResponse<Complaint> response = PaginatedResponse.<Complaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            // for the connected user
            } else if(user.equals("me")){
                Page<Complaint> results = complaintService.getAllComplaintsByUserPaginated(userDetails.getUser().getId(), pageable);
                PaginatedResponse<Complaint> response = PaginatedResponse.<Complaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            } else {
                if(!user.matches("[0-9]+"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameter");
                Page<Complaint> results = complaintService.getAllComplaintsByUserPaginated(Long.parseLong(user), pageable);
                PaginatedResponse<Complaint> response = PaginatedResponse.<Complaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            }   
        }
        // Building complaints
        else if(type.equals("building")){
            // for all users
            if(user == null || user.equals("all")){
                Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsPaginated(pageable);
                PaginatedResponse<BuildingComplaint> response = PaginatedResponse.<BuildingComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            // for the connected user
            } else if(user.equals("me")){
                Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByUserPaginated(userDetails.getUser().getId(), pageable);
                PaginatedResponse<BuildingComplaint> response = PaginatedResponse.<BuildingComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            } else {
                if(!user.matches("[0-9]+"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameter");
                Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByUserPaginated(Long.parseLong(user), pageable);
                PaginatedResponse<BuildingComplaint> response = PaginatedResponse.<BuildingComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            }
        }
        // Room complaints
        else if(type.equals("room")){
            // for all users
            if(user == null || user.equals("all")){
                Page<RoomComplaint> results = complaintService.getAllRoomComplaintsPaginated(pageable);
                PaginatedResponse<RoomComplaint> response = PaginatedResponse.<RoomComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            // for the connected user
            } else if(user.equals("me")){
                Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByUserPaginated(userDetails.getUser().getId(), pageable);
                PaginatedResponse<RoomComplaint> response = PaginatedResponse.<RoomComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            } else {
                if(!user.matches("[0-9]+"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameter");
                Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByUserPaginated(Long.parseLong(user), pageable);
                PaginatedResponse<RoomComplaint> response = PaginatedResponse.<RoomComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            }
        }
        // Facility complaints
        else if(type.equals("facilities")){
            // for all users
            if(user == null || user.equals("all")){
                Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsPaginated(pageable);
                PaginatedResponse<FacilitiesComplaint> response = PaginatedResponse.<FacilitiesComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            // for the connected user
            } else if(user.equals("me")){
                Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByUserPaginated(userDetails.getUser().getId(), pageable);
                PaginatedResponse<FacilitiesComplaint> response = PaginatedResponse.<FacilitiesComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            } else {
                if(!user.matches("[0-9]+"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameter");
                Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByUserPaginated(Long.parseLong(user), pageable);
                PaginatedResponse<FacilitiesComplaint> response = PaginatedResponse.<FacilitiesComplaint>builder()
                    .results(results.getContent())
                    .page(results.getNumber())
                    .totalPages(results.getTotalPages())
                    .count(results.getNumberOfElements())
                    .totalItems(results.getTotalElements())
                    .last(results.isLast())
                    .build();
                return response;
            }
        }
        // Unknown type
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type parameter");
        }
    }

    @GetMapping("/complaints/{id}")
    Complaint getComplaint(@PathVariable Long id) {
        return complaintService.getComplaint(id);
    }
    
    @PatchMapping("/complaints/{id}/handling")
    @PreAuthorize("hasAuthority('ADEI')")
    Complaint handleComplaint(@PathVariable Long id, @Valid @RequestBody EditComplaintStatusAndHandlerDto editComplaintStatusAndHandlerDto) {
        return complaintService.editComplaintStatusAndHandler(editComplaintStatusAndHandlerDto, id);
    }

    @PatchMapping("/complaints/{id}/details")
    Complaint editComplaintDetails(@PathVariable Long id, @Valid @RequestBody CreateComplaintDto createComplaintDto, @AuthenticationPrincipal MyUserDetails userDetails) {
        if(createComplaintDto instanceof CreateBuildingComplaintDto){
            return complaintService.editBuildingComplaintDetails((CreateBuildingComplaintDto) createComplaintDto, id, userDetails.getUser());
        } else if(createComplaintDto instanceof CreateRoomComplaintDto){
            return complaintService.editRoomComplaintDetails((CreateRoomComplaintDto) createComplaintDto, id, userDetails.getUser());
        } else if(createComplaintDto instanceof CreateFacilityComplaintDto){
            return complaintService.editFacilitiesComplaintDetails((CreateFacilityComplaintDto) createComplaintDto, id, userDetails.getUser());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid complaint type");
        }
    }
    
    @DeleteMapping("/complaints/{id}")
    MessageResponse deleteComplaint(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails userDetails) {
        complaintService.deleteComplaint(id, userDetails.getUser());
        return MessageResponse.builder().message("Complaint deleted successfully").build();
    }
}
