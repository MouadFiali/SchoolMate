package com.manager.schoolmateapi.alerts;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.schoolmateapi.SchoolMateApiApplication;
import com.manager.schoolmateapi.alerts.dto.CreateAlertDto;
import com.manager.schoolmateapi.alerts.dto.EditAlertDto;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.data.geo.Point;
import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.alerts.enumerations.AlertType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SchoolMateApiApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlertsControllerTest {

        @Autowired
        MockMvc mockMvc;
        @Autowired
        ObjectMapper objectMapper;
        @Autowired
        AlertRepository alertRepository;
        @Autowired
        UserRepository userRepository;
        MyUserDetails testUser;
        MyUserDetails anotherTestUser;

        @BeforeEach
        @Transactional
        public void setup() {
                alertRepository.deleteAll();
                userRepository.deleteAll();
                // create test user
                User user = new User();
                user.setFirstName("Zakaria");
                user.setLastName("Boukernafa");
                user.setRole(UserRole.STUDENT);
                user.setPassword("123456A@");
                user.setEmail("zakaria.hachm@gmail.com");
                User anotherUser = new User();
                anotherUser.setFirstName("Mehdi");
                anotherUser.setLastName("essalehi");
                anotherUser.setRole(UserRole.STUDENT);
                anotherUser.setPassword("123456A@");//
                anotherUser.setEmail("mehdi.essalehi@gmail.com");
                anotherUser.setActive(true);

                // save test user
                testUser = new MyUserDetails(userRepository.save(user));
                anotherTestUser = new MyUserDetails(userRepository.save(anotherUser));
                // create some alerts
                // build alert
                Alert alert = new Alert();
                alert.setTitle("test alert");
                alert.setDescription("test alert description");
                alert.setType(AlertType.DANGER);
                alert.setCoordinates(new Point(1, 1));
                alert.setStatus(AlertStatus.PENDING);
                alert.setUser(testUser.getUser());
                // save the test alert
                alertRepository.save(alert);

        }

        @AfterEach
        @Transactional
        public void postTest() {
                alertRepository.deleteAll();
                userRepository.deleteAll();
        }

        @Test // test create an alert with all required fields
        public void testCreateAlert_shouldReturnCreatedAlert() throws Exception {
                CreateAlertDto alertDto = CreateAlertDto.builder()
                                .title("test alert")
                                .description("test alert description")
                                .type(AlertType.DANGER)
                                .coordinates(List.of(1.0, 1.0))
                                // .status(AlertStatus.PENDING)
                                .build();

                String response = mockMvc.perform(post("/alerts")
                                .with(user(testUser))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(alertDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.title").value("test alert"))
                                .andExpect(jsonPath("$.description").value("test alert description"))
                                .andExpect(jsonPath("$.type").value("DANGER"))
                                .andExpect(jsonPath("$.coordinates.x").value(1.0))
                                .andExpect(jsonPath("$.coordinates.y").value(1.0))
                                .andExpect(jsonPath("$.status").value("PENDING"))
                                .andReturn().getResponse().getContentAsString();
                // Delete the alert after the test
                alertRepository.deleteById(objectMapper.readValue(response, Alert.class).getId());

        }

        @Test // test to get all alerts for a user
        public void testGetAllAlerts_shouldReturnListOfAlerts() throws Exception {
                CreateAlertDto alertDto = CreateAlertDto.builder()
                                .title("test alert")
                                .description("test alert description")
                                .type(AlertType.DANGER)
                                .coordinates(List.of(1.0, 1.0))
                                // .status(AlertStatus.PENDING)
                                .build();

                int page = 1;
                int pageSize = 0;
                String response = mockMvc.perform(get("/alerts")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(pageSize))
                                .with(user(testUser))
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(alertDto)))
                                .andExpect(status().isOk())
                                // .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                // .andExpect(jsonPath("$[0].title").value("test alert"))
                                // .andExpect(jsonPath("$[0].description").value("test alert description"))
                                // .andExpect(jsonPath("$[0].type").value("DANGER"))
                                // .andExpect(jsonPath("$[0].coordinates.x").value(1.0))
                                // .andExpect(jsonPath("$[0].coordinates.y").value(1.0))
                                // .andExpect(jsonPath("$[0].status").value("PENDING"))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                PaginatedResponse<Alert> paginatedResponse = (PaginatedResponse<Alert>) objectMapper.readValue(response,
                                PaginatedResponse.class);
                assertEquals(paginatedResponse.getResults().size(), paginatedResponse.getCount());
                assertEquals(paginatedResponse.getPage(), page);
                assertEquals(paginatedResponse.getCount(), pageSize);
                assertTrue(paginatedResponse.isLast());
        }

        @Test // test to get an alert by ID
        public void testGetAlertById_shouldReturnAlert() throws Exception {

                // create an new alert

                Alert alert = new Alert();
                alert.setTitle("test alert");
                alert.setDescription("test alert description");
                alert.setType(AlertType.DANGER);
                alert.setCoordinates(new Point(1, 1));
                alert.setStatus(AlertStatus.PENDING);
                alert.setUser(testUser.getUser());
                // save the test alert
                alert = alertRepository.save(alert);

                mockMvc.perform(get("/alerts/" + alert.getId())
                                .with(user(testUser))
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                // .andExpect(content().contentType("application/json"))
                                .andExpect(jsonPath("$.id").value(alert.getId()))
                                .andExpect(jsonPath("$.title").value(alert.getTitle()))
                                .andExpect(jsonPath("$.description").value(alert.getDescription()))
                                .andExpect(jsonPath("$.type").value(alert.getType().toString()))
                                .andExpect(jsonPath("$.coordinates.x").value(alert.getCoordinates().getX()))
                                .andExpect(jsonPath("$.coordinates.y").value(alert.getCoordinates().getY()))
                                .andExpect(jsonPath("$.status").value(alert.getStatus().toString()))
                                .andReturn();

                // Delete the alert after the test
                alertRepository.deleteById(alert.getId());
        }

        // test alert by id not found
        @Test
        public void testGetAlertById_shouldReturnNotFound() throws Exception {
                mockMvc.perform(get("/api/alerts/999999999")
                                .with(user(testUser)))
                                // .contentType("application/json")
                                .andExpect(status().isNotFound())
                                .andReturn();
        }

        @Test // test to update an alert
        public void testUpdateAlert_shouldReturnUpdatedAlert() throws Exception {
                // create an new alert

                Alert alert = new Alert();
                alert.setTitle("test alert");
                alert.setDescription("test alert description");
                alert.setType(AlertType.DANGER);
                alert.setCoordinates(new Point(1, 1));
                alert.setStatus(AlertStatus.PENDING);
                alert.setUser(testUser.getUser());
                // save the test alert
                alert = alertRepository.save(alert);
                // create an edit alert dto

                EditAlertDto editAlertDto = EditAlertDto.builder()
                                .title("updated title")
                                .description("updated description")
                                .type(AlertType.WARNING)
                                .coordinates(List.of(2.0, 2.0))
                                .status(AlertStatus.PENDING)
                                .build();
                // save the updated alert

                mockMvc.perform(patch("/alerts/" + alert.getId())
                                .with(user(testUser))
                                .contentType("application/json")
                                // .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(editAlertDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(alert.getId()))
                                .andExpect(jsonPath("$.title").value("updated title"))
                                .andExpect(jsonPath("$.description").value("updated description"))
                                .andExpect(jsonPath("$.type").value("WARNING"))
                                .andExpect(jsonPath("$.coordinates.x").value(2.0))
                                .andExpect(jsonPath("$.coordinates.x").value(2.0))
                                .andExpect(jsonPath("$.status").value("PENDING"))// check why the other fields are not
                                                                                 // updated
                                .andReturn();
                // Delete the alert after the test
                alertRepository.deleteById(alert.getId());
        }

        @Test // test to cancel an alert
        public void testCancelAlert_shouldReturnCancelledAlert() throws Exception {
                // create a test alert
                Alert alert = new Alert();
                alert.setTitle("test alert");
                alert.setDescription("test alert description");
                alert.setType(AlertType.DANGER);
                alert.setCoordinates(new Point(1, 1));
                alert.setStatus(AlertStatus.PENDING);
                alert.setUser(testUser.getUser());
                alertRepository.save(alert);

                mockMvc.perform(patch("/alerts/" + alert.getId() + "/cancel")
                                .with(user(testUser)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(alert.getId()))
                                .andExpect(jsonPath("$.status").value("CANCELLED"))
                                .andReturn();
        }

        @Test // test confirm an alert
        public void testConfirmAlert_shouldReturnConfirmedAlert() throws Exception {
                // create a test alert
                Alert alert = new Alert();
                alert.setTitle("test alert");
                alert.setDescription("test alert description");
                alert.setType(AlertType.DANGER);
                alert.setCoordinates(new Point(1, 1));
                alert.setStatus(AlertStatus.PENDING);
                alert.setUser(testUser.getUser());
                alertRepository.save(alert);

                // confirm the alert
                String response = mockMvc.perform(patch("/alerts/" + alert.getId() + "/confirm")
                                .with(user(testUser))
                                .contentType("application/json"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("test alert"))
                                .andExpect(jsonPath("$.description").value("test alert description"))
                                .andExpect(jsonPath("$.type").value("DANGER"))
                                .andExpect(jsonPath("$.coordinates.x").value(1.0))
                                .andExpect(jsonPath("$.coordinates.y").value(1.0))
                                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                                .andReturn().getResponse().getContentAsString();

                // delete the alert after the test
                alertRepository.deleteById(objectMapper.readValue(response, Alert.class).getId());
        }

        // test to delete an alert
        @Test
        public void testDeleteAlert_shouldReturnNoContent() throws Exception {
                // create a test alert
                Alert alert = new Alert();
                alert.setTitle("test alert");
                alert.setDescription("test alert description");
                alert.setType(AlertType.DANGER);
                alert.setCoordinates(new Point(1, 1));
                alert.setStatus(AlertStatus.PENDING);
                alert.setUser(testUser.getUser());
                alertRepository.save(alert);

                mockMvc.perform(delete("/alerts/" + alert.getId())
                                .with(user(testUser))).andReturn();

                // check if the alert is deleted
                mockMvc.perform(get("/alerts/" + alert.getId())
                                .with(user(testUser)))
                                .andExpect(status().isNotFound())
                                .andReturn();

        }
}
