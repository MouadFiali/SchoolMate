package com.manager.schoolmateapi.placesuggestions;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.placesuggestions.dto.CreatePlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.dto.EditPlaceSuggestionDto;
import com.manager.schoolmateapi.mappers.PlaceSuggestionsMapper;
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

    public PlaceSuggestions getSuggestionById(Long id, User user) {
        return PlaceSuggestionRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER);
    }

    public Iterable<PlaceSuggestions> getAllPlaceSuggestions() {
        return PlaceSuggestionRepository.findAll();
    }

    public PlaceSuggestions addUserPlaceSuggestion(CreatePlaceSuggestionDto createPlaceSuggestionDto, User user) {
        PlaceSuggestions placesuggestion = dtoMapper.createDTOtoPlaceSuggestion(createPlaceSuggestionDto);
        placesuggestion.setUser(user);
        return PlaceSuggestionRepository.save(placesuggestion);
    }

    public PlaceSuggestions editUserPlaceSuggestion(Long id, EditPlaceSuggestionDto editPlaceSuggestionDto, User user) {

        PlaceSuggestions placesuggestion = PlaceSuggestionRepository.findByIdAndUser(id, user)
                .orElseThrow(NOT_FOUND_HANDLER);
        dtoMapper.updatePlaceSuggestionFromDto(editPlaceSuggestionDto, placesuggestion);
        PlaceSuggestionRepository.save(placesuggestion);
        return placesuggestion;
    }

    public void deleteUserPlaceSuggestions(Long id, User user) {
        PlaceSuggestionRepository.delete(PlaceSuggestionRepository.findByIdAndUser(id, user).orElseThrow(NOT_FOUND_HANDLER));
    }
}