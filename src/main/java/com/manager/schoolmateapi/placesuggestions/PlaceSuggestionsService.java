package com.manager.schoolmateapi.placesuggestions;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.mappers.PlaceSuggestionsMapper;
import com.manager.schoolmateapi.placesuggestions.dto.CreatePlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.dto.EditPlaceSuggestionDto;
import com.manager.schoolmateapi.users.models.User;

@Service

public class PlaceSuggestionsService {
    private static Supplier<ResponseStatusException> NOT_FOUND_HANDLER = () -> {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "PlaceSuggestion not found");
    };

    @Autowired
    private PlaceSuggestionsMapper dtoMapper;
    @Autowired
    private PlaceSuggestionRepository PlaceSuggestionRepository;

    public PlaceSuggestions getSuggestionById(Long id) {
        return PlaceSuggestionRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER);
    }

    public Page<PlaceSuggestions> getAllPlaceSuggestions(Pageable pageable) {
        return PlaceSuggestionRepository.findAll(pageable);
    }

    public Page<PlaceSuggestions> getUserPlaceSuggestionsById(Long id, Pageable pageable){
        return PlaceSuggestionRepository.findByUserId(id, pageable);
    }

    public Page<PlaceSuggestions> getUserPlaceSuggestions(User user, Pageable pageable){
        return PlaceSuggestionRepository.findByUser(user, pageable);
    }

    public PlaceSuggestions addUserPlaceSuggestion(CreatePlaceSuggestionDto createPlaceSuggestionDto, User user) {
        PlaceSuggestions placesuggestion = dtoMapper.createDTOtoPlaceSuggestion(createPlaceSuggestionDto);
        placesuggestion.setUser(user);
        return PlaceSuggestionRepository.save(placesuggestion);
    }

    public PlaceSuggestions editUserPlaceSuggestion(Long id, EditPlaceSuggestionDto editPlaceSuggestionDto) {

        PlaceSuggestions placesuggestion = PlaceSuggestionRepository.findById(id)
                .orElseThrow(NOT_FOUND_HANDLER);
        dtoMapper.updatePlaceSuggestionFromDto(editPlaceSuggestionDto, placesuggestion);
        PlaceSuggestionRepository.save(placesuggestion);
        return placesuggestion;
    }

    
    public void deletePlaceSuggestion(Long id){
        PlaceSuggestionRepository.delete(PlaceSuggestionRepository.findById(id).orElseThrow(NOT_FOUND_HANDLER));
    }
}