package com.manager.schoolmateapi.alerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;
import com.manager.schoolmateapi.utils.MessageResponse;

import jakarta.validation.Valid;

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
    public Iterable<Alert> getAllAlerts(){
        return alertService.getAllAlerts();
    }
    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.CREATED)
    Alert addaAlert(@Valid @RequestBody CreateAlertDto createAlertDto){
        return alertService.addAlert(createAlertDto);
        
    }
    @GetMapping(value="/alerts/{id}")
    Alert  getAlert(@PathVariable("id") Long id) {
       return alertService.getAlertById(id);
    }
    @PatchMapping("/alerts/{id}")
    Alert updateAlert(
        @PathVariable("id") Long id,
        @Valid @RequestBody EditAlertDto editAlertDto){
            return alertService.editAlert(id, editAlertDto);
        }

        
    @PatchMapping("/alerts/{id}/cancel")
    Alert cancelAlert(@PathVariable("id") Long id) throws Exception{
         return alertService.cancelAlert(id);
    }

    @PatchMapping("/alerts/{id}/confirm")
    Alert confirmAlert(@PathVariable("id") Long id) throws Exception{
         return alertService.confirmAlert(id);
        }
    

    @DeleteMapping("/alerts/{id}")
    MessageResponse deleteAlert(@PathVariable("id") Long id){
        alertService.deleteAlert(id);
        return new MessageResponse("Alert deleted successfully");
    }
    

}
