package com.manager.schoolmateapi.alerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

@RestController
public class AlertController {
    @Autowired
    AlertService alertService;

    @GetMapping(value = "/alerts")
    public PaginatedResponse<Alert> getAllUserAlerts(@AuthenticationPrincipal MyUserDetails userDetails,
            Pageable pageable) {

        Page<Alert> results = alertService.getAllUserAlertsPaginated(userDetails.getUser(), pageable);
        PaginatedResponse<Alert> response = PaginatedResponse.<Alert>builder()
                .results(results.getContent())
                .page(results.getNumber())
                .count(results.getNumberOfElements())
                .totalPages(results.getTotalPages())
                .totalItems(results.getTotalElements())
                .last(results.isLast())
                .build();
        return response;
    }

    //get all the alert that the status is CONFIRMED

    @GetMapping("/allalerts")
    public PaginatedResponse<Alert> getAllAlerts(Pageable pageable) {
        Page<Alert> results = alertService.getAllAlertsPaginated(pageable);
        PaginatedResponse<Alert> response = PaginatedResponse.<Alert>builder()
                .results(results.getContent())
                .page(results.getNumber())
                .count(results.getNumberOfElements())
                .totalPages(results.getTotalPages())
                .totalItems(results.getTotalElements())
                .last(results.isLast())
                .build();
        return response;
    }
    //get all the alerts that the status is PENDING
    @GetMapping("/pendingalerts")
    public PaginatedResponse<Alert> getPendingAlerts(Pageable pageable) {
        Page<Alert> results = alertService.getPendingAlertsPaginated(pageable);
        PaginatedResponse<Alert> response = PaginatedResponse.<Alert>builder()
                .results(results.getContent())
                .page(results.getNumber())
                .count(results.getNumberOfElements())
                .totalPages(results.getTotalPages())
                .totalItems(results.getTotalElements())
                .last(results.isLast())
                .build();
        return response;
    }   

    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.CREATED)
    Alert addaUserAlert(@AuthenticationPrincipal MyUserDetails userDetails,
            @Valid @RequestBody CreateAlertDto createAlertDto) {
        return alertService.addUserAlert(createAlertDto, userDetails.getUser());

    }

    @GetMapping(value = "/alerts/{id}")
    Alert getUserAlert(@PathVariable("id") Long id) {
        return alertService.getAlertById(id);
    }

    @PatchMapping("/alerts/{id}")
    Alert updateUserAlert(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable("id") Long id,
            @Valid @RequestBody EditAlertDto editAlertDto) {
        return alertService.editUserAlert(id, editAlertDto, userDetails.getUser());
    }

    @PatchMapping("/alerts/{id}/cancel")
    Alert cancelUserAlert(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id)
            throws Exception {
        return alertService.cancelUserAlert(id, userDetails.getUser());
    }

    @PatchMapping("/alerts/{id}/confirm")
    Alert confirmUserAlert(@PathVariable("id") Long id)
            throws Exception {
        return alertService.confirmUserAlert(id);
    }

    @DeleteMapping("/alerts/{id}")
    MessageResponse deleteUserAlert(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable("id") Long id) {
        alertService.deleteUserAlert(id, userDetails.getUser());
        return new MessageResponse("Alert deleted successfully");
    }

}
