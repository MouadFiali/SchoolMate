package com.manager.schoolmateapi.placesuggestions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.manager.schoolmateapi.placesuggestions.dto.CreatePlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.dto.EditPlaceSuggestionDto;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.utils.MessageResponse;

import jakarta.validation.Valid;

@RestController
public class PlaceSuggestionsController {
    @Autowired
    PlaceSuggestionsService placesuggestionsService;

    @GetMapping(value = "/placesuggestions")
    public Iterable<PlaceSuggestions> getAllPlaceSuggestions() {
        return placesuggestionsService.getAllPlaceSuggestions();
    }

    @PostMapping("/placesuggestions")
    @ResponseStatus(HttpStatus.CREATED)
    PlaceSuggestions addaUserPlaceSuggestion(@AuthenticationPrincipal MyUserDetails userDetails,
            @Valid @RequestBody CreatePlaceSuggestionDto createPlaceSuggestionDto) {
        return placesuggestionsService.addUserPlaceSuggestion(createPlaceSuggestionDto, userDetails.getUser());

    }

    @GetMapping(value = "/placesuggestions/{id}")
    PlaceSuggestions getUserPlaceSuggestions(@AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable("id") Long id) {
        return placesuggestionsService.getSuggestionById(id, userDetails.getUser());
    }

    @PatchMapping("/placesuggestions/{id}")
    PlaceSuggestions updateUserPlaceSuggestion(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable("id") Long id,
            @Valid @RequestBody EditPlaceSuggestionDto editPlaceSuggestionDto) {
        return placesuggestionsService.editUserPlaceSuggestion(id, editPlaceSuggestionDto, userDetails.getUser());
    }

    @DeleteMapping("/placesuggestions/{id}")
    MessageResponse deleteUserPlaceSuggestions(@AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable("id") Long id) {
        placesuggestionsService.deleteUserPlaceSuggestions(id, userDetails.getUser());
        return new MessageResponse("Place Suggestions deleted successfully");
    }

}