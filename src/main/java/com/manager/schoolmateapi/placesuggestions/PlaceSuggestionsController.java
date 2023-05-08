package com.manager.schoolmateapi.placesuggestions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import com.manager.schoolmateapi.placesuggestions.dto.CreatePlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.dto.EditPlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.utils.MessageResponse;
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

import jakarta.validation.Valid;

@RestController
public class PlaceSuggestionsController {
    @Autowired
    PlaceSuggestionsService placesuggestionsService;

    @GetMapping(value = "/placesuggestions")
        public PaginatedResponse<PlaceSuggestions> getAllPlaceSuggestions(Pageable pageable, @RequestParam(required = false, value="type") List<PlaceSuggestionType> types) {
            
            Page<PlaceSuggestions> results = placesuggestionsService.getAllPlaceSuggestions(pageable);
            
            PaginatedResponse<PlaceSuggestions> response = PaginatedResponse.<PlaceSuggestions>builder()
            .results(results.getContent())
            .page(results.getNumber())
            .totalPages(results.getTotalPages())
            .count(results.getNumberOfElements())
            .totalItems(results.getTotalElements())
            .last(results.isLast())
            .build();
    
            return response;
        }  

    @PostMapping("/placesuggestions")
    @ResponseStatus(HttpStatus.CREATED)
    PlaceSuggestions addaUserPlaceSuggestion(@AuthenticationPrincipal MyUserDetails userDetails,
            @Valid @RequestBody CreatePlaceSuggestionDto createPlaceSuggestionDto) {
        return placesuggestionsService.addUserPlaceSuggestion(createPlaceSuggestionDto, userDetails.getUser());

    }

    @GetMapping(value="placesuggestions")
    public PaginatedResponse<PlaceSuggestions> getMyUserPlaceSuggestions(Pageable pageable, @RequestParam(required = false, value="type") List<PlaceSuggestionType> types,@AuthenticationPrincipal MyUserDetails userDetails ){
        Page<PlaceSuggestions> results = placesuggestionsService.getUserPlaceSuggestions(userDetails.getUser(), pageable);

        PaginatedResponse<PlaceSuggestions> response = PaginatedResponse.<PlaceSuggestions>builder()
        .results(results.getContent())
        .page(results.getNumber())
        .totalPages(results.getTotalPages())
        .count(results.getNumberOfElements())
        .totalItems(results.getTotalElements())
        .last(results.isLast())
        .build();

        return response;
    }

    @GetMapping(value = "/placesuggestions/user/{id}")
    public PaginatedResponse<PlaceSuggestions> getUserPlaceSuggestions(Pageable pageable, @RequestParam(required = false, value="type") List<PlaceSuggestionType> types, @PathVariable("id") Long id){
        Page<PlaceSuggestions> results = placesuggestionsService.getUserPlaceSuggestionsById(id, pageable);

        PaginatedResponse<PlaceSuggestions> response = PaginatedResponse.<PlaceSuggestions>builder()
        .results(results.getContent())
        .page(results.getNumber())
        .totalPages(results.getTotalPages())
        .count(results.getNumberOfElements())
        .totalItems(results.getTotalElements())
        .last(results.isLast())
        .build();

        return response;
    }

    @GetMapping(value = "/placesuggestions/{id}")
    public PlaceSuggestions getPlaceSuggestion(@PathVariable("id") Long id){
        return placesuggestionsService.getSuggestionById(id);
    }

    @PatchMapping("/placesuggestions/{id}")
    PlaceSuggestions updatePlaceSuggestion(
            @PathVariable("id") Long id,
            @Valid @RequestBody EditPlaceSuggestionDto editPlaceSuggestionDto) {
        return placesuggestionsService.editUserPlaceSuggestion(id, editPlaceSuggestionDto);
    }

    @DeleteMapping("/placesuggestions/{id}")
    MessageResponse deleteUserPlaceSuggestion(@PathVariable("id") Long id) {
        placesuggestionsService.deletePlaceSuggestion(id);
        return new MessageResponse("Place Suggestion deleted successfully");
    }

}