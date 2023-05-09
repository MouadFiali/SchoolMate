package com.manager.schoolmateapi.users;

import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.schoolmateapi.SchoolMateApiApplication;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SchoolMateApiApplication.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

    @Autowired
	UserRepository userRepository;

    MyUserDetails testUser;

    @BeforeAll
    public void setUp() throws Exception {
        // Create a user
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john_doe@um5.ac.ma");
        user.setPassword("password");
        user.setActive(true);
        user.setRole(UserRole.STUDENT);
        
        // Create another user
        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane_doe@um5.ac.ma");
        user2.setPassword("password");
        user2.setActive(true);
        user2.setRole(UserRole.STUDENT);

        // Create a third user
        User user3 = new User();
        user3.setFirstName("John");
        user3.setLastName("Smith");
        user3.setEmail("john_smith@um5.ac.ma");
        user3.setPassword("password");
        user3.setActive(true);
        user3.setRole(UserRole.ADEI);


        // Save the users
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);

        // Create a test user
        testUser = new MyUserDetails(user3);
    }

    @Test // Test search by first name
    public void testGetUsersBySearch_withFirstName() throws Exception {
        int pageSize = 2;
        int page = 0;


        mockMvc.perform(get("/users?search=John")
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.results[*].firstName", Matchers.everyItem(Matchers.is("John"))))
            .andReturn();
            
    }

    @Test // Test search by last name (case insensitive)
    public void testGetUsersBySearch_withLastName() throws Exception {
        int pageSize = 2;
        int page = 0;

        mockMvc.perform(get("/users?search=doe") // Search for "doe" in the last name (case insensitive)
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.results[*].lastName", Matchers.everyItem(Matchers.is("Doe"))))
            .andReturn();
    }

    @Test // Test search by email (case insensitive)
    public void testGetUsersBySearch3() throws Exception {

        // Create some users with an email containing "mouad"
        User user = new User();
        user.setFirstName("User"); // force the first name to be different from the search term
        user.setLastName("Boukhriss");
        user.setEmail("mouad_boukhriss@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        User user2 = new User();
        user2.setFirstName("User"); // force the first name to be different from the search term
        user2.setLastName("Samawi");
        user2.setEmail("hamdoun_mouadi@um5.ac.ma");
        user2.setPassword("password");
        user2.setRole(UserRole.STUDENT);
        
        // Save the users
        userRepository.save(user);
        userRepository.save(user2);

        int pageSize = 2;
        int page = 0;

        mockMvc.perform(get("/users?search=mouad") // Search for "mouad" in the email (case insensitive)
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.results[*].email", Matchers.everyItem(Matchers.containsString("mouad"))))
            .andReturn();

        // Delete the users
        userRepository.delete(user);
        userRepository.delete(user2);
    }

    @Test // Test search by first name and role is given
    public void testGetUsersBySearchRoleGiven() throws Exception {

        // We have 2 users with the first name "John" but their roles are different
        // Let's create a user with the first name "John" and the role STUDENT
        User user = new User();
        user.setFirstName("John");
        user.setLastName("hamdoun");
        user.setEmail("john_hamdoun@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);
        
        // Save the user
        userRepository.save(user);

        int pageSize = 2;
        int page = 0;

        mockMvc.perform(get("/users?search=John&role=" + UserRole.STUDENT) // Search for "John" in the first name (or even email) and role is STUDENT
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.results[*].firstName", Matchers.everyItem(Matchers.is("John"))))
            .andExpect(jsonPath("$.results[*].role", Matchers.everyItem(Matchers.is("STUDENT"))))
            .andReturn();
        
        // Delete the user
        userRepository.delete(user);
    }

    @Test // Test search by role only
    public void testGetUsersBySearchRoleOnly() throws Exception {

        // We have 2 users with the role STUDENT and 1 user with the role ADEI
        // Let's create a user with the role STUDENT
        User user = new User();
        user.setFirstName("John");
        user.setLastName("hamdoun");
        user.setEmail("hamdoun@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // Save the user
        userRepository.save(user);

        int pageSize = 3; // We have 3 users with the role STUDENT
        int page = 0;

        mockMvc.perform(get("/users?role=" + UserRole.STUDENT) // Get users with the role STUDENT
            .with(user(testUser))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(pageSize))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results", Matchers.hasSize(3)))
            .andExpect(jsonPath("$.results[*].role", Matchers.everyItem(Matchers.is("STUDENT"))))
            .andReturn();
    }



    @AfterAll
    public void tearDown() throws Exception {
        // Delete all users
        userRepository.deleteAll();
    }
    
}
