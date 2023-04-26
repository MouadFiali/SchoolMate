package com.manager.schoolmateapi.complaints;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
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
    
}
