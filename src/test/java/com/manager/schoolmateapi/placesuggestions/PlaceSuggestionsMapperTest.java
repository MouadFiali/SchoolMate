package com.manager.schoolmateapi.placesuggestions;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;

import com.manager.schoolmateapi.mappers.PlaceSuggestionsMapper;
import com.manager.schoolmateapi.placesuggestions.dto.CreatePlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.dto.EditPlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;

@SpringBootTest
public class PlaceSuggestionsMapperTest {

    @Autowired
    PlaceSuggestionsMapper suggestionMapper;

    @Test
    public void testCreatePlaceSuggestionFromDto_shouldReturnPlaceSuggestionSuccesfully() {
        CreatePlaceSuggestionDto createPlaceSuggetionDto = new CreatePlaceSuggestionDto().builder()
                .description("this is a description")
                .suggestiontype(PlaceSuggestionType.StudyPlace)
                .coordinates(List.of(1.0, 1.0))
                .build();
        PlaceSuggestions suggestion = suggestionMapper.createDTOtoPlaceSuggestion(createPlaceSuggetionDto);
        assert (suggestion.getDescription().equals(createPlaceSuggetionDto.getDescription()));
        assert (suggestion.getSuggestiontype().equals(createPlaceSuggetionDto.getSuggestiontype()));
        assertThat(suggestion.getCoordinates(), Matchers.is(PlaceSuggestionsMapper.listToPoint(createPlaceSuggetionDto.getCoordinates())));
    }

    @Test
    public void testEditPlaceSuggestionFromDto_shouldReturnChangedPlaceSuggestion(){
        EditPlaceSuggestionDto editPlaceSuggestionDto = new EditPlaceSuggestionDto()
        .builder()
        .description("description")
        .suggestiontype(PlaceSuggestionType.Entertainment)
        .coordinates(List.of(1.0, 1.0))
        .build();

        //create the old suggestion
        PlaceSuggestions suggestion = new PlaceSuggestions();
        suggestion.setDescription("old description");
        suggestion.setSuggestiontype(PlaceSuggestionType.StudyPlace);
        suggestion.setCoordinates(new Point(2, 2));
        suggestionMapper.updatePlaceSuggestionFromDto(editPlaceSuggestionDto, suggestion);
        assert(suggestion.getDescription().equals(editPlaceSuggestionDto.getDescription()));
        assert(suggestion.getSuggestiontype().equals(editPlaceSuggestionDto.getSuggestiontype()));
        assertThat(suggestion.getCoordinates(),Matchers.is(editPlaceSuggestionDto.getCoordinates()));

    }    
}
