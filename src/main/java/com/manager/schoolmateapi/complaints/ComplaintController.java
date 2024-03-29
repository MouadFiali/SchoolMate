package com.manager.schoolmateapi.complaints;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusAndHandlerDto;
import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;
import com.manager.schoolmateapi.complaints.models.BuildingComplaint;
import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;
import com.manager.schoolmateapi.complaints.models.RoomComplaint;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.services.UserService;
import com.manager.schoolmateapi.utils.MessageResponse;
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

import jakarta.validation.Valid;

@RestController
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserService userService;

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

    // endpoint to get the count of complaints by handler
    @GetMapping("/complaints/count-by-handler/{id}")
    @PreAuthorize("hasAuthority('ADEI')")
    Long getComplaintsCountByHandler(@PathVariable Long id){
        Page<Complaint> results = complaintService.getAllComplaintsByHandlerIdPaginated(id, Pageable.unpaged());
        return results.getTotalElements();
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

    // Get complaints by status
    @GetMapping("/complaints-by-status")
    @PreAuthorize("hasAuthority('ADEI')")
    PaginatedResponse<?> getComplaintsByStatusAndComplainant(
        @AuthenticationPrincipal MyUserDetails userDetails,
        Pageable pageable,
        @RequestParam(required = false) ComplaintStatus status, 
        @RequestParam(required = false) String user,
        @RequestParam(required = false) String type){
            // If no status is provided
            if(status == null){
                // All complaints
                if (type == null || type.equals("all")){
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
                        Page<Complaint> results = complaintService.getAllComplaintsByHandlerIdPaginated(userDetails.getUser().getId(), pageable);
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
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<Complaint> results = complaintService.getAllComplaintsByHandlerIdPaginated(Long.parseLong(user), pageable);
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
                        Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByHandlerIdPaginated(userDetails.getUser().getId(), pageable);
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
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByHandlerIdPaginated(Long.parseLong(user), pageable);
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
                    // Room complaints
                } else if(type.equals("room")){
                    // for all users
                    if(user == null || user.equals("all")){
                        Page<RoomComplaint> results = complaintService.getAllRoomComplaintsPaginated(pageable);
                        PaginatedResponse<RoomComplaint> response = PaginatedResponse.<RoomComplaint>builder()
                            .results(results.getContent())
                            .page(results.getNumber())
                            .totalPages(results.getTotalPages())
                            .count (results.getNumberOfElements())
                            .totalItems(results.getTotalElements())
                            .last(results.isLast())
                            .build();
                        return response;
                    }
                    // for the connected user
                    else if(user.equals("me")){
                        Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByHandlerIdPaginated(userDetails.getUser().getId(), pageable);
                        PaginatedResponse<RoomComplaint> response = PaginatedResponse.<RoomComplaint>builder()
                            .results(results.getContent())
                            .page(results.getNumber())
                            .totalPages(results.getTotalPages())
                            .count (results.getNumberOfElements())
                            .totalItems(results.getTotalElements())
                            .last(results.isLast())
                            .build();
                        return response;
                    } else {
                        if(!user.matches("[0-9]+"))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameter");
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByHandlerIdPaginated(Long.parseLong(user), pageable);
                        PaginatedResponse<RoomComplaint> response = PaginatedResponse.<RoomComplaint>builder()
                            .results(results.getContent())
                            .page(results.getNumber())
                            .totalPages(results.getTotalPages())
                            .count (results.getNumberOfElements())
                            .totalItems(results.getTotalElements())
                            .last(results.isLast())
                            .build();
                        return response;
                    }
                    // Facilities complaints
                } else if(type.equals("facilities")){
                    // for all users
                    if(user == null || user.equals("all")){
                        Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsPaginated(pageable);
                        PaginatedResponse<FacilitiesComplaint> response = PaginatedResponse.<FacilitiesComplaint>builder()
                            .results(results.getContent())
                            .page(results.getNumber())
                            .totalPages(results.getTotalPages())
                            .count (results.getNumberOfElements())
                            .totalItems(results.getTotalElements())
                            .last(results.isLast())
                            .build();
                        return response;
                    }
                    // for the connected user
                    else if(user.equals("me")){
                        Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByHandlerIdPaginated(userDetails.getUser().getId(), pageable);
                        PaginatedResponse<FacilitiesComplaint> response = PaginatedResponse.<FacilitiesComplaint>builder()
                            .results(results.getContent())
                            .page(results.getNumber())
                            .totalPages(results.getTotalPages())
                            .count (results.getNumberOfElements())
                            .totalItems(results.getTotalElements())
                            .last(results.isLast())
                            .build();
                        return response;
                    } else {
                        if(!user.matches("[0-9]+"))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user parameter");
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByHandlerIdPaginated(Long.parseLong(user), pageable);
                        PaginatedResponse<FacilitiesComplaint> response = PaginatedResponse.<FacilitiesComplaint>builder()
                            .results(results.getContent())
                            .page(results.getNumber())
                            .totalPages(results.getTotalPages())
                            .count (results.getNumberOfElements())
                            .totalItems(results.getTotalElements())
                            .last(results.isLast())
                            .build();
                        return response;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type parameter");
                }
            }
            // Pending complaints
            else if(status.equals(ComplaintStatus.PENDING)){
                // All complaints
                if(type == null || type.equals("all")){
                    Page<Complaint> results = complaintService.getAllComplaintsByStatusPaginated(status, pageable);
                    PaginatedResponse<Complaint> response = PaginatedResponse.<Complaint>builder()
                        .results(results.getContent())
                        .page(results.getNumber())
                        .totalPages(results.getTotalPages())
                        .count(results.getNumberOfElements())
                        .totalItems(results.getTotalElements())
                        .last(results.isLast())
                        .build();
                    return response;
                // Building complaints (for all users as pending complaints are not assigned to a user)
                } else if(type.equals("building")){
                    Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByStatusPaginated(status, pageable);
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
                // Room complaints (for all users as pending complaints are not assigned to a user)
                else if(type.equals("room")){
                    Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByStatusPaginated(status, pageable);
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
                // Facility complaints (for all users as pending complaints are not assigned to a user)
                else if(type.equals("facilities")){
                    Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByStatusPaginated(status, pageable);
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
                // Unknown type
                else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type parameter");
                }
            }
            // Not pending complaints (they have a handler)
            else {
                // All complaints
                if(type == null || type.equals("all")){
                    // for all users
                    if(user == null || user.equals("all")){
                        Page<Complaint> results = complaintService.getAllComplaintsByStatusPaginated(status, pageable);
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
                        Page<Complaint> results = complaintService.getAllComplaintsByStatusAndUserPaginated(status, userDetails.getUser().getId(), pageable);
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
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<Complaint> results = complaintService.getAllComplaintsByStatusAndUserPaginated(status, Long.parseLong(user), pageable);
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
                        Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByStatusPaginated(status, pageable);
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
                        Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByStatusAndUserPaginated(status, userDetails.getUser().getId(), pageable);
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
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<BuildingComplaint> results = complaintService.getAllBuildingComplaintsByStatusAndUserPaginated(status, Long.parseLong(user), pageable);
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
                    // Room complaints
                } else if(type.equals("room")){
                    // for all users
                    if(user == null || user.equals("all")){
                        Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByStatusPaginated(status, pageable);
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
                        Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByStatusAndUserPaginated(status, userDetails.getUser().getId(), pageable);
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
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<RoomComplaint> results = complaintService.getAllRoomComplaintsByStatusAndUserPaginated(status, Long.parseLong(user), pageable);
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
                    // Facility complaints
                } else if(type.equals("facilities")){
                    // for all users
                    if(user == null || user.equals("all")){
                        Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByStatusPaginated(status, pageable);
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
                        Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByStatusAndUserPaginated(status, userDetails.getUser().getId(), pageable);
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
                        else if(!userService.getUser(Long.parseLong(user)).getRole().equals(UserRole.ADEI))
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an ADEI member");
                        Page<FacilitiesComplaint> results = complaintService.getAllFacilitiesComplaintsByStatusAndUserPaginated(status, Long.parseLong(user), pageable);
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
                else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type parameter");
                }
            }

        }



    @GetMapping("/complaints/{id}")
    Complaint getComplaint(@PathVariable Long id) {
        return complaintService.getComplaint(id);
    }
    
    @PatchMapping("/complaints/{id}/handling")
    @PreAuthorize("hasAuthority('ADEI')")
    Complaint handleComplaint(@PathVariable Long id, 
        @Valid @RequestBody EditComplaintStatusAndHandlerDto editComplaintStatusAndHandlerDto,
        @AuthenticationPrincipal MyUserDetails userDetails) {
        return complaintService.editComplaintStatusAndHandler(editComplaintStatusAndHandlerDto, id, userDetails.getUser());
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
