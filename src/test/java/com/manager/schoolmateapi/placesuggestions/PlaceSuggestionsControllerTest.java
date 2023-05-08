package com.manager.schoolmateapi.placesuggestions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.geo.Point;
import org.springframework.test.web.servlet.MockMvc;
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

public class PlaceSuggestionsControllerTest {

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
                userRepository.deleteAll();
                placeSuggestionRepository.deleteAll();
                // creating testUser1
                User User1 = new User();
                User1.setFirstName("Mouad");
                User1.setLastName("EL OUARTI");
                User1.setRole(UserRole.STUDENT);
                User1.setPassword("123456@ME");
                User1.setEmail("mouad_elouarti@um5.ac.ma");

                // creating testUser2
                User User2 = new User();
                User2.setFirstName("Mehdi");
                User2.setLastName("ESSALEHI");
                User2.setRole(UserRole.STUDENT);
                User2.setPassword("123456@ME");
                User2.setEmail("mehdi_essalehi@um5.ac.ma");
                User2.setActive(true);

                // save test user
                testUser1 = new MyUserDetails(userRepository.save(User1));
                testUser2 = new MyUserDetails(userRepository.save(User2));
        }

        @AfterAll
        public void cleanup(){
                userRepository.deleteAll();
                placeSuggestionRepository.deleteAll();
        }
 
        @Test // Testing the creation of a Place Suggestion with all of the required fields
        public void testCreatePlaceSuggestion_shouldReturnCreatedPlaceSuggestion() throws Exception {
                CreatePlaceSuggestionDto placeSuggestionDto = CreatePlaceSuggestionDto.builder()
                                .description("This is a description")
                                .suggestiontype(PlaceSuggestionType.StudyPlace)
                                .coordinates(List.of(1.0, 1.0))
                                .build();

                mockMvc.perform(post("/placesuggestions")
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
                placeSuggestionRepository.deleteAll();
        }

        @Test // Testing the creation of a Place Suggestion with a missing description
        public void testCreatePlaceSuggestion_shouldReturnMissingDescriptionError() throws Exception {
                CreatePlaceSuggestionDto placeSuggestionDto = CreatePlaceSuggestionDto.builder()
                                .suggestiontype(PlaceSuggestionType.StudyPlace)
                                .coordinates(List.of(1.0, 1.0))
                                .build();

                mockMvc.perform(post("/placesuggestions")
                                .with(user(testUser1))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(placeSuggestionDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors").value("A description is required"))
                                .andReturn();
                // Delete the Suggestion after the test
                placeSuggestionRepository.deleteAll();
        }

        @Test // Testing the creation of a Place Suggestion with a missing type
        public void testCreatePlaceSuggestion_shouldReturnMissingTypeError() throws Exception {
                CreatePlaceSuggestionDto placeSuggestionDto = CreatePlaceSuggestionDto.builder()
                                .description("this is a description")
                                .coordinates(List.of(1.0, 1.0))
                                .build();

                mockMvc.perform(post("/placesuggestions")
                                .with(user(testUser1))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(placeSuggestionDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors").value("The category is necessary"))
                                .andReturn();
                // Delete the Suggestion after the test
                placeSuggestionRepository.deleteAll();
        }

        @Test // Testing the creation of a Place Suggestion with a missing type
        public void testCreatePlaceSuggestion_shouldReturnMissingCoordinatesError() throws Exception {
                CreatePlaceSuggestionDto placeSuggestionDto = CreatePlaceSuggestionDto.builder()
                                .description("this is a description")
                                .suggestiontype(PlaceSuggestionType.FoodPlace)
                                .build();

                mockMvc.perform(post("/placesuggestions")
                                .with(user(testUser1))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(placeSuggestionDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors").value("The coordinates are required"))
                                .andReturn();
                // Delete the Suggestion after the test
                placeSuggestionRepository.deleteAll();
        }

        @Test // test to get all place suggestions
        public void testGetAllPlaceSuggestions_shouldReturnListOfPlaceSuggestions() throws Exception {

                placeSuggestionRepository.deleteAll();

                // building a Place Suggestion
                PlaceSuggestions suggestion = new PlaceSuggestions();
                suggestion.setDescription("This is a description");
                suggestion.setSuggestiontype(PlaceSuggestionType.StudyPlace);
                suggestion.setCoordinates(new Point(1, 1));
                suggestion.setUser(testUser1.getUser());

                // building a Place Suggestion
                PlaceSuggestions suggestion2 = new PlaceSuggestions();
                suggestion2.setDescription("This is a description");
                suggestion2.setSuggestiontype(PlaceSuggestionType.StudyPlace);
                suggestion2.setCoordinates(new Point(1, 1));
                suggestion2.setUser(testUser2.getUser());

                // save the test suggestion
                placeSuggestionRepository.save(suggestion);
                placeSuggestionRepository.save(suggestion2);

                mockMvc.perform(get("/placesuggestions")
                                .with(user(testUser1))
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.results.size()").value(2))
                                .andExpect(content().contentType("application/json"))
                                .andReturn();

                // Delete all afterwards
                placeSuggestionRepository.deleteAll();
        }

        @Test // test to get all place suggestions of a user
        public void testGetAllUserPlaceSuggestions_shouldReturnListOfPlaceSuggestions() throws Exception {

                placeSuggestionRepository.deleteAll();

                // building a Place Suggestion
                PlaceSuggestions suggestion = new PlaceSuggestions();
                suggestion.setDescription("This is a description");
                suggestion.setSuggestiontype(PlaceSuggestionType.StudyPlace);
                suggestion.setCoordinates(new Point(1, 1));
                suggestion.setUser(testUser1.getUser());

                // building a Place Suggestion
                PlaceSuggestions suggestion2 = new PlaceSuggestions();
                suggestion2.setDescription("This is a description");
                suggestion2.setSuggestiontype(PlaceSuggestionType.StudyPlace);
                suggestion2.setCoordinates(new Point(1, 1));
                suggestion2.setUser(testUser2.getUser());

                // save the test suggestion
                placeSuggestionRepository.save(suggestion);
                placeSuggestionRepository.save(suggestion2);

                mockMvc.perform(get("/placesuggestions/user/"+testUser1.getUser().getId())
                                .with(user(testUser1))
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.results.size()").value(1))
                                .andExpect(content().contentType("application/json"))
                                .andReturn();

                // Delete all afterwards
                placeSuggestionRepository.deleteAll();
        }

        @Test // Testing Getting a Place Suggestion by ID
        public void testGetPlaceSuggestionById_shouldReturnPlaceSuggestion() throws Exception {

                placeSuggestionRepository.deleteAll();

                // creating a new Place Suggestion
                PlaceSuggestions suggestion = new PlaceSuggestions();
                suggestion.setDescription("test suggestion description");
                suggestion.setSuggestiontype(PlaceSuggestionType.StudyPlace);
                suggestion.setCoordinates(new Point(1, 1));
                suggestion.setUser(testUser1.getUser());

                // save the test suggestion
                suggestion = placeSuggestionRepository.save(suggestion);

                mockMvc.perform(get("/placesuggestions/" + suggestion.getId())
                                .with(user(testUser1))
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/json"))
                                .andExpect(jsonPath("$.id").value(suggestion.getId()))
                                .andExpect(jsonPath("$.description").value("test suggestion description"))
                                .andExpect(jsonPath("$.suggestiontype").value("StudyPlace"))
                                .andExpect(jsonPath("$.coordinates.x").value(1.0))
                                .andExpect(jsonPath("$.coordinates.x").value(1.0))
                                .andReturn();

                // Delete the suggestion after the test
                placeSuggestionRepository.deleteAll();
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

                placeSuggestionRepository.deleteAll();

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

                // save the updated suggestion
                mockMvc.perform(patch("/placesuggestions/" + suggestion.getId())
                                .with(user(testUser1))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(editPlaceSuggestionDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(suggestion.getId()))
                                .andExpect(jsonPath("$.description").value("updated suggestion description"))
                                .andExpect(jsonPath("$.suggestiontype").value("Other"))
                                .andExpect(jsonPath("$.coordinates.x").value(2.0))
                                .andExpect(jsonPath("$.coordinates.x").value(2.0))
                                .andReturn();

                // Delete the suggestion after the test
                placeSuggestionRepository.deleteAll();
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

                // Saving the suggestions to the repository
                placeSuggestionRepository.save(suggestion);

                // Deleting the suggestion
                mockMvc.perform(delete("/placesuggestions/" + suggestion.getId())
                                .with(user(testUser1))).andReturn();

                // check if the suggestion is deleted
                mockMvc.perform(get("/placesuggestions/" + suggestion.getId())
                                .with(user(testUser1)))
                                .andExpect(status().isNotFound())
                                .andReturn();

        }
}