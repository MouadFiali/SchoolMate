package com.manager.schoolmateapi.alerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;
import com.manager.schoolmateapi.utils.MessageResponse;

import jakarta.validation.Valid;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
public class AlertController {
    @Autowired
    AlertService alertService;
    @GetMapping(value="/alerts")
    public Iterable<Alert> getAllUserAlerts( @AuthenticationPrincipal MyUserDetails userDetails){
        return alertService.getAllUserAlerts(userDetails.getUser());
    }
    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.CREATED)
    Alert addaUserAlert( @AuthenticationPrincipal MyUserDetails userDetails,@Valid @RequestBody CreateAlertDto createAlertDto){
        return alertService.addUserAlert(createAlertDto, userDetails.getUser());
        
    }
    @GetMapping(value="/alerts/{id}")
    Alert  getUserAlert( @AuthenticationPrincipal MyUserDetails userDetails,@PathVariable("id") Long id) {
       return alertService.getAlertById(,id, userDetails.getUser());
    }
    @PatchMapping("/alerts/{id}")
    Alert updateUserAlert(
        @AuthenticationPrincipal MyUserDetails userDetails,
        @PathVariable("id") Long id,
        @Valid @RequestBody EditAlertDto editAlertDto){
            return alertService.editUserAlert(id, editAlertDto, userDetails.getUser());
        }

        
    @PatchMapping("/alerts/{id}/cancel")
    Alert cancelUserAlert(@AuthenticationPrincipal MyUserDetails userDetails,@PathVariable("id") Long id) throws Exception{
         return alertService.cancelUserAlert(id, userDetails.getUser());
    }

    @PatchMapping("/alerts/{id}/confirm")
    Alert confirmUserAlert(@AuthenticationPrincipal MyUserDetails userDetails,@PathVariable("id") Long id) throws Exception{
         return alertService.confirmUserAlert(id, userDetails.getUser());
        }
    

    @DeleteMapping("/alerts/{id}")
    MessageResponse deleteUserAlert(@AuthenticationPrincipal MyUserDetails userDetails,@PathVariable("id") Long id){
        alertService.deleteUserAlert(id, userDetails.getUser());
        return new MessageResponse("Alert deleted successfully");
    }
    

}
