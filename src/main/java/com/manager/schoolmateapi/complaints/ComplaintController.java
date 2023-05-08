package com.manager.schoolmateapi.complaints;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusAndHandlerDto;
import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.utils.MessageResponse;

import jakarta.validation.Valid;

@RestController
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping("/complaints")
    ResponseEntity<?> addComplaint(@Valid @RequestBody CreateComplaintDto createComplaintDto, @AuthenticationPrincipal MyUserDetails userDetails) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        if(createComplaintDto instanceof CreateBuildingComplaintDto){
            Complaint complaint = complaintService.addBuildingComplaint((CreateBuildingComplaintDto) createComplaintDto, userDetails.getUser());
            return ResponseEntity.created(location).body(complaint);
        } else if(createComplaintDto instanceof CreateRoomComplaintDto){
            Complaint complaint = complaintService.addRoomComplaint((CreateRoomComplaintDto) createComplaintDto, userDetails.getUser());
            return ResponseEntity.created(location).body(complaint);
        } else if(createComplaintDto instanceof CreateFacilityComplaintDto){
            Complaint complaint = complaintService.addFacilitiesComplaint((CreateFacilityComplaintDto) createComplaintDto, userDetails.getUser());
            return ResponseEntity.created(location).body(complaint);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid complaint type"));
        }
    }

    @GetMapping("/complaints")
    ResponseEntity<?> getComplaints(@AuthenticationPrincipal MyUserDetails userDetails, @RequestParam(required = false) String type, 
                @RequestParam(required = false) String user) {
        //All compaints
        if(type == null || type.equals("all")){
            // for all users
            if(user == null || user.equals("all")){
                return ResponseEntity.ok(complaintService.getAllComplaints());
            // for the connected user
            } else if(user.equals("me")){
                return ResponseEntity.ok(complaintService.getAllComplaintsByUser(userDetails.getUser().getId()));
            } else {
                if(!user.matches("[0-9]+"))
                    return ResponseEntity.badRequest().body(new MessageResponse("Invalid user parameter"));
                return ResponseEntity.ok(complaintService.getAllComplaintsByUser(Long.parseLong(user)));
            }
        // Building complaints
        } else if(type.equals("building")){
            // for all users
            if(user == null || user.equals("all")){
                return ResponseEntity.ok(complaintService.getAllBuildingComplaints());
            // for the connected user
            } else if(user.equals("me")){
                return ResponseEntity.ok(complaintService.getAllBuildingComplaintsByUser(userDetails.getUser().getId()));
            } else {
                if(!user.matches("[0-9]+"))
                    return ResponseEntity.badRequest().body(new MessageResponse("Invalid user parameter"));
                return ResponseEntity.ok(complaintService.getAllBuildingComplaintsByUser(Long.parseLong(user)));
            }
        }
        // Room complaints
        else if(type.equals("room")){
            // for all users
            if(user == null || user.equals("all")){
                return ResponseEntity.ok(complaintService.getAllRoomComplaints());
            // for the connected user
            } else if(user.equals("me")){
                return ResponseEntity.ok(complaintService.getAllRoomComplaintsByUser(userDetails.getUser().getId()));
            } else {
                if(!user.matches("[0-9]+"))
                    return ResponseEntity.badRequest().body(new MessageResponse("Invalid user parameter"));
                return ResponseEntity.ok(complaintService.getAllRoomComplaintsByUser(Long.parseLong(user)));
            }
        }
        // Facilities complaints
        else if(type.equals("facilities")){
            // for all users
            if(user == null || user.equals("all")){
                return ResponseEntity.ok(complaintService.getAllFacilitiesComplaints());
            // for the connected user
            } else if(user.equals("me")){
                return ResponseEntity.ok(complaintService.getAllFacilitiesComplaintsByUser(userDetails.getUser().getId()));
            } else {
                if(!user.matches("[0-9]+"))
                    return ResponseEntity.badRequest().body(new MessageResponse("Invalid user parameter"));
                return ResponseEntity.ok(complaintService.getAllFacilitiesComplaintsByUser(Long.parseLong(user)));
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid type parameter"));
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
    void deleteComplaint(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails userDetails) {
        complaintService.deleteComplaint(id, userDetails.getUser());
    }
}
