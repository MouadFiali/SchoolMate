package com.manager.schoolmateapi.placesuggestions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.geo.Point;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.schoolmateapi.SchoolMateApiApplication;
import com.manager.schoolmateapi.placesuggestions.dto.CreatePlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.dto.EditPlaceSuggestionDto;
import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SchoolMateApiApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class PlaceSuggestionsControllerTest{

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PlaceSuggestionRepository placeSuggestionRepository;
    @Autowired
    UserRepository userRepository;
    MyUserDetails testUser1;
    MyUserDetails testUser2;

    @BeforeAll
    @Transactional
    public void setup() {
            placeSuggestionRepository.deleteAll();
            userRepository.deleteAll();

            // creating testUser1
            User User1 = new User();
            User1.setFirstName("Mouad");
            User1.setLastName("EL OUARTI");
            User1.setRole(UserRole.STUDENT);
            User1.setPassword("123456@ME");
            User1.setEmail("mouadelouarti1@gmail.com");

            // creating testUser2
            User User2 = new User();
            User2.setFirstName("Mehdi");
            User2.setLastName("ESSALEHI");
            User2.setRole(UserRole.STUDENT);
            User2.setPassword("123456@ME");
            User2.setEmail("mehdi.essalehi@gmail.com");
            User2.setActive(true);

            // save test user
            testUser1 = new MyUserDetails(userRepository.save(User1));
            testUser2 = new MyUserDetails(userRepository.save(User2));

            // creating PlaceSuggestions
            
            // building a Place Suggestion
            PlaceSuggestions suggestion = new PlaceSuggestions();
            suggestion.setDescription("This is a description");
            suggestion.setSuggestiontype(PlaceSuggestionType.StudyPlace);
            suggestion.setCoordinates(new Point(1, 1));
            suggestion.setUser(testUser1.getUser());

            // save the test suggestion
            placeSuggestionRepository.save(suggestion);
    }

        @Test // Testing the creation of a Place Suggestion with all of the required fields
        public void testCreatePlaceSuggestion_shouldReturnCreatedPlaceSuggestion() throws Exception {
                CreatePlaceSuggestionDto placeSuggestionDto = CreatePlaceSuggestionDto.builder()
                                .description("This is a description")
                                .suggestiontype(PlaceSuggestionType.StudyPlace)
                                .coordinates(List.of(1.0, 1.0))
                                .build();

                String response = mockMvc.perform(post("/placesuggestions")
                                .with(user(testUser1))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(placeSuggestionDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.description").value("This is a description"))
                                .andExpect(jsonPath("$.suggestiontype").value("StudyPlace"))
                                .andExpect(jsonPath("$.coordinates.x").value(1.0))
                                .andExpect(jsonPath("$.coordinates.y").value(1.0))
                                .andReturn().getResponse().getContentAsString();

                // Delete the Suggestion after the test
                placeSuggestionRepository.deleteById(objectMapper.readValue(response, PlaceSuggestions.class).getId());

        }
        @Test // test to get all place suggestions for a user
        public void testGetAllPlaceSuggestions_shouldReturnListOfPlaceSuggestions() throws Exception {
                CreatePlaceSuggestionDto placeSuggestionDto = CreatePlaceSuggestionDto.builder()
                                .description("This is a test description")
                                .suggestiontype(PlaceSuggestionType.Other)
                                .coordinates(List.of(1.0, 1.0))
                                .build();
                mockMvc.perform(get("/placesuggestions")
                                .with(user(testUser1))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(placeSuggestionDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].description").value("This is a test description"))
                                .andExpect(jsonPath("$[0].suggestiontype").value("Other"))
                                .andExpect(jsonPath("$[0].coordinates.x").value(1.0))
                                .andExpect(jsonPath("$[0].coordinates.y").value(1.0))
                                .andReturn();
        }

        @Test // Testing Getting a Place Suggestion by ID
        public void testGetPlaceSuggestionById_shouldReturnPlaceSuggestion() throws Exception {

                //creating a new Place Suggestion

                PlaceSuggestions suggestion = new PlaceSuggestions();
                suggestion.setDescription("test suggestion description");
                suggestion.setSuggestiontype(PlaceSuggestionType.StudyPlace);
                suggestion.setCoordinates(new Point(1, 1));
                suggestion.setUser(testUser1.getUser());

                // save the test suggestion
                suggestion = placeSuggestionRepository.save(suggestion);

                mockMvc.perform(get("/placesuggestion/" + suggestion.getId())
                                .with(user(testUser1))
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(suggestion.getId()))
                                .andExpect(jsonPath("$.description").value(suggestion.getDescription()))
                                .andExpect(jsonPath("$.suggestiontype").value(suggestion.getSuggestiontype().toString()))
                                .andExpect(jsonPath("$.coordinates.x").value(suggestion.getCoordinates().getX()))
                                .andExpect(jsonPath("$.coordinates.y").value(suggestion.getCoordinates().getY()))
                                .andReturn();

                // Delete the suggestion after the test
                placeSuggestionRepository.deleteById(suggestion.getId());
        }

        // test suggestion by id not found
        @Test
        public void testGetPlaceSuggestionById_shouldReturnNotFound() throws Exception {
                mockMvc.perform(get("/api/placesuggestions/999999999")
                                .with(user(testUser1)))
                                .andExpect(status().isNotFound())
                                .andReturn();
        }

        @Test // testing updating a suggestion
        public void testUpdatePlaceSuggestion_shouldReturnUpdatedPlaceSuggestion() throws Exception {

                // creating a new suggestion
                PlaceSuggestions suggestion = new PlaceSuggestions();
                suggestion.setDescription("test suggestion description");
                suggestion.setSuggestiontype(PlaceSuggestionType.Entertainment);
                suggestion.setCoordinates(new Point(1, 1));
                suggestion.setUser(testUser1.getUser());

                // save the test suggestion
                suggestion = placeSuggestionRepository.save(suggestion);

                // create an edit suggestion dto
                EditPlaceSuggestionDto editPlaceSuggestionDto = EditPlaceSuggestionDto.builder()
                                .description("updated suggestion description")
                                .suggestiontype(PlaceSuggestionType.Other)
                                .coordinates(new Point(2, 2))
                                .build();

                //save the updated suggestion
                mockMvc.perform(patch("/placesuggestions/" + suggestion.getId())
                                .with(user(testUser1))
                                .contentType("application/json")
                                // .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(editPlaceSuggestionDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(suggestion.getId()))
                                .andExpect(jsonPath("$.description").value("updated suggested description"))
                                .andExpect(jsonPath("$.suggestiontype").value("Other"))
                                .andExpect(jsonPath("$.coordinates.x").value(2.0))
                                .andExpect(jsonPath("$.coordinates.x").value(2.0))
                                .andReturn();
                                
                // Delete the suggestion after the test
                placeSuggestionRepository.deleteById(suggestion.getId());
        }

          // testing deleting a place suggestion
          @Test
          public void testDeletePlaceSuggestion_shouldReturnNoContent() throws Exception {
                 // creating a new suggestion
                PlaceSuggestions suggestion = new PlaceSuggestions();
                suggestion.setDescription("test suggestion description");
                suggestion.setSuggestiontype(PlaceSuggestionType.Entertainment);
                suggestion.setCoordinates(new Point(1, 1));
                suggestion.setUser(testUser1.getUser());

                //Saving the suggestions to the repository
                placeSuggestionRepository.save(suggestion);
  
                //Deleting the suggestion
                  mockMvc.perform(delete("/placesuggestions/" + suggestion.getId())
                                  .with(user(testUser1))).andReturn();
  
                  // check if the suggestion is deleted
                  mockMvc.perform(get("/placesuggestions/" + suggestion.getId())
                                  .with(user(testUser1)))
                                  .andExpect(status().isNotFound())
                                  .andReturn();
  
          }
}