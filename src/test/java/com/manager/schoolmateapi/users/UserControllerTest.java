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

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.schoolmateapi.SchoolMateApiApplication;
import com.manager.schoolmateapi.users.dto.CreateUserDto;
import com.manager.schoolmateapi.users.dto.EditUserDto;
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

    // Store the created user ids to delete them after the tests
    ArrayList<Long> createdUsersIds = new ArrayList<>();

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

        // Add the created users ids to the list
        createdUsersIds.add(user.getId());
        createdUsersIds.add(user2.getId());
        createdUsersIds.add(user3.getId());

        // Create a test user
        testUser = new MyUserDetails(user3);
    }

    @Test // create a user with a valid email
    public void testCreateUser_withValidEmail() throws Exception {
        CreateUserDto user = CreateUserDto.builder()
            .firstName("Mouad")
            .lastName("FIALI")
            .email("mouad_fiali@um5.ac.ma") // Valid email
            .password("P@ssw0rd")
            .confirmPassowrd("P@ssw0rd")
            .build();

        String response = mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())))
                    .andReturn().getResponse().getContentAsString();

        // Delete the user
        Long id = new JSONObject(response).getLong("id");
        userRepository.deleteById(id);
    }

    @Test // create a user with an existing email
    public void testCreateUser_withExistingEmail() throws Exception {
        CreateUserDto user = CreateUserDto.builder()
            .firstName("Mouad")
            .lastName("FIALI")
            .email("john_doe@um5.ac.ma") // Existing email
            .password("P@ssw0rd")
            .confirmPassowrd("P@ssw0rd")
            .build();

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail", Matchers.is("Email already exists")))
                    .andReturn();
        
        // no user should be created
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
        user.setEmail("hamdoun_johnathan@um5.ac.ma");
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

        // Delete the user
        userRepository.delete(user);
    }

    @Test // Test get user by id
    public void testGetUserById_shouldReturnUser() throws Exception {

        // Create a user
        User user = new User();
        user.setFirstName("mouad");
        user.setLastName("fiali");
        user.setEmail("mouad_fiali@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // Save the user
        userRepository.save(user);

        // Get the user by id
        mockMvc.perform(get("/users/" + user.getId())
            .with(user(testUser))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", Matchers.is("mouad")))
            .andExpect(jsonPath("$.lastName", Matchers.is("fiali")))
            .andExpect(jsonPath("$.email", Matchers.is("mouad_fiali@um5.ac.ma")))
            .andExpect(jsonPath("$.role", Matchers.is("STUDENT")))
            .andReturn();

        // Delete the user
        userRepository.delete(user);
    }

    @Test // Test get user by id with an invalid id
    public void testGetUserById_shouldReturnNotFound() throws Exception {

        // Get the user by id
        mockMvc.perform(get("/users/0")
            .with(user(testUser))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", Matchers.is("User not found")))
            .andReturn();
    
    }

    @Test // test login with a valid user
    public void testLogin_shouldReturnStatusOK() throws Exception {

        // Create a json object with the email and password of the test user
        JSONObject json = new JSONObject();
        json.put("username", "john_doe@um5.ac.ma");
        json.put("password", "password");
        
        // login with the test user
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", Matchers.is("Logged in successfully")))
            .andReturn();

    }

    @Test // test edit user by id when it's not the principal
    public void testEditUserById_shouldReturnForbidden() throws Exception {

        EditUserDto editUserDto = EditUserDto.builder()
            .firstName("john le bon")
            .lastName("doe le bien")
            .build();

        // create a user
        User user = new User();
        user.setFirstName("Mouad");
        user.setLastName("Fiali");
        user.setEmail("mouad_fiali@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // save the user
        userRepository.save(user);

        // edit the user
        mockMvc.perform(patch("/users/" + user.getId())
            .with(user(testUser))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserDto)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.detail", Matchers.is("You are not authorized to edit this user")))
            .andReturn();
        
        // delete the user
        userRepository.delete(user);
    }

    @Test // test edit user by id when it's the principal
    public void testEditUserById_shouldReturnUser() throws Exception {

        EditUserDto editUserDto = EditUserDto.builder()
            .firstName("john le bon")
            .lastName("doe le bien")
            .build();

         // create a user
         User user = new User();
         user.setFirstName("mike");
         user.setLastName("ross");
         user.setEmail("mike_ross@um5.ac.ma");
         user.setPassword("password");
         user.setRole(UserRole.STUDENT);
 
         // save the user
         user = userRepository.save(user);

         MyUserDetails userDetails = new MyUserDetails(user);

        // edit the user
        mockMvc.perform(patch("/users/" + userDetails.getUser().getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", Matchers.is("john le bon")))
            .andExpect(jsonPath("$.lastName", Matchers.is("doe le bien")))
            .andReturn();
        
        // delete the user
        userRepository.delete(user);

    }

    @Test // test edit the user's role by Moderator
    public void testEditUserRole_shouldReturnUser() throws Exception {

        EditUserDto editUserRoleDto = EditUserDto.builder()
            .role(UserRole.ADEI)
            .build();

        // create a user
        User user = new User();
        user.setFirstName("Mouad");
        user.setLastName("Fiali");
        user.setEmail("mouad_fiali@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // create a moderator
        User moderator = new User();
        moderator.setFirstName("moderator");
        moderator.setLastName("moderator");
        moderator.setEmail("moderator@um5.ac.ma");
        moderator.setPassword("password");
        moderator.setRole(UserRole.MODERATOR);

        // save the user
        userRepository.save(user);
        userRepository.save(moderator);

        MyUserDetails userDetails = new MyUserDetails(moderator);

        // edit the user's role
        mockMvc.perform(patch("/users/" + user.getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserRoleDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role", Matchers.is("ADEI")))
            .andReturn();

        // delete the user
        userRepository.delete(user);
        userRepository.delete(moderator);

    }

    @Test // test edit the user's role by himself
    public void testEditUserRole_shouldReturnForbidden() throws Exception {

        EditUserDto editUserDto = EditUserDto.builder()
            .firstName("john le bon")
            .lastName("doe le bien")
            .role(UserRole.ADEI)
            .build();

         // create a user
         User user = new User();
         user.setFirstName("Mouad");
         user.setLastName("Fiali");
         user.setEmail("mouad_fiali@um5.ac.ma");
         user.setPassword("password");
         user.setRole(UserRole.STUDENT);
 
         // save the user
         userRepository.save(user);

         MyUserDetails userDetails = new MyUserDetails(user);

        // edit the user
        mockMvc.perform(patch("/users/" + user.getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserDto)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.detail", Matchers.is("You are not authorized to edit this user")))
            .andReturn();

        // delete the user
        userRepository.delete(user);
    }

    @Test // test edit user's details by another moderator
    public void testEditUserDetails_shouldReturnForbidden() throws Exception {

        EditUserDto editUserRoleDto = EditUserDto.builder()
            .role(UserRole.ADEI)
            .email("bad_email@um5.ac.ma")
            .build();

        // create a user
        User user = new User();
        user.setFirstName("Mouad");
        user.setLastName("Fiali");
        user.setEmail("mouad_fiali@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // create a moderator
        User moderator = new User();
        moderator.setFirstName("moderator");
        moderator.setLastName("moderator");
        moderator.setEmail("moderator@um5.ac.ma");
        moderator.setPassword("password");
        moderator.setRole(UserRole.MODERATOR);

        // save the user
        userRepository.save(user);
        userRepository.save(moderator);

        MyUserDetails userDetails = new MyUserDetails(moderator);

        // edit the user's role
        mockMvc.perform(patch("/users/" + user.getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserRoleDto)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.detail", Matchers.is("You can only change the role of the user")))
            .andReturn();

        // delete the user
        userRepository.delete(user);
        userRepository.delete(moderator);

    }

    @Test // test /me endpoint after updating the user's details
    public void testMe_shouldReturnUpdatedUser() throws Exception {

        EditUserDto editUserDto = EditUserDto.builder()
            .firstName("john le bon")
            .lastName("doe le bien")
            .build();

        // create a user
        User user = new User();
        user.setFirstName("mike");
        user.setLastName("ross");
        user.setEmail("mike_ross@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // save the user
        userRepository.save(user);

        MyUserDetails userDetails = new MyUserDetails(user);

        // edit the user
        mockMvc.perform(patch("/users/" + user.getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editUserDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", Matchers.is("john le bon")))
            .andExpect(jsonPath("$.lastName", Matchers.is("doe le bien")))
            .andReturn();

        // Get the user's details after updating them
        mockMvc.perform(get("/me")
            .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", Matchers.is("john le bon")))
            .andExpect(jsonPath("$.lastName", Matchers.is("doe le bien")))
            .andReturn();

        // delete the user
        userRepository.delete(user);
    }

    @Test // test login with an invalid user
    public void testLogin_shouldReturnStatusBadRequest() throws Exception {

        // Create a json object with the email and password of the test user
        JSONObject json = new JSONObject();
        json.put("username", "john_doe@um5.ac.ma");
        json.put("password", "wrong_password");

        // login with the test user
        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.is("Incorrect username or password")))
            .andReturn();
    }

    @Test // test delete user by id by ROLE ADEI
    public void testDeleteUserById_shouldReturnForbidden() throws Exception {

        // Create a user
        User user = new User();
        user.setFirstName("mouad");
        user.setLastName("fiali");
        user.setEmail("mouad_fiali@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // Save the user
        userRepository.save(user);

        // Delete the user by id
        mockMvc.perform(delete("/users/" + user.getId())
            .with(user(testUser))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
        
        // Delete the user
        userRepository.delete(user);
    }

    @Test // test delete user by id by ROLE MODERATOR
    public void testDeleteUserById_shouldReturnDeleted() throws Exception {

        // Create a user
        User user = new User();
        user.setFirstName("delete");
        user.setLastName("delete");
        user.setEmail("delete@um5.ac.ma");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        // create a moderator
        User moderator = new User();
        moderator.setFirstName("mehdi");
        moderator.setLastName("essalehi");
        moderator.setEmail("mehdi_essalehi@um5.ac.ma");
        moderator.setPassword("password");
        moderator.setRole(UserRole.MODERATOR);


        // Save the user
        userRepository.save(user);
        userRepository.save(moderator);

        MyUserDetails userDetails = new MyUserDetails(moderator);

        // Delete the user by id
        mockMvc.perform(delete("/users/" + user.getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", Matchers.is("User deleted successfully")))
            .andReturn();

        // get the user by id
        mockMvc.perform(get("/users/" + user.getId())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", Matchers.is("User not found")))
            .andReturn();

        // Delete the moderator
        userRepository.delete(moderator);
    }

    @AfterAll
    public void tearDown() throws Exception {
        // Delete the created users from the list we created
        System.out.println("All users tests are done!");
        System.out.println("Deleting the created users...");
        for (Long userId : createdUsersIds) {
            userRepository.deleteById(userId);
            System.out.println("User with id " + userId + " deleted successfully");
        }
        System.out.println("Database is cleaned up!");
    }
    
}
