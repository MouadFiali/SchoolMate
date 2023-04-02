package com.manager.schoolmateapi.documents;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.StreamSupport;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.documents.repositories.DocumentTagsRepository;
import com.manager.schoolmateapi.documents.repositories.DocumentsRepository;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DocumentsControllerTest {

  private static String DUMMY_PDF_PATH = "src/test/resources/dummy.pdf";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  DocumentsRepository documentsRepository;

  @Autowired
  DocumentTagsRepository documentTagsRepository;

  @Autowired
  UserRepository userRepository;

  MyUserDetails testUser;

  List<Long> createdTagsIds;

  @BeforeEach
  void setup() {
    documentsRepository.deleteAll();
    documentTagsRepository.deleteAll();
    // Create some tags
    Iterable<DocumentTag> tags = documentTagsRepository.saveAll(
        List.of(
            DocumentTag.builder().name("Théorie des graphes").build(),
            DocumentTag.builder().name("Compilation").build(),
            DocumentTag.builder().name("1ère année").build(),
            DocumentTag.builder().name("2ème année").build()));

    createdTagsIds = StreamSupport.stream(tags.spliterator(), false)
        .map(tag -> tag.getId()).toList();

    // Create a test user
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Smith");
    user.setRole(UserRole.STUDENT);
    user.setPassword("password");
    user.setEmail("john.smith@gmail.com");

    // Save the test user
    User newUser = userRepository.save(user);
    testUser = new MyUserDetails(newUser);
  }

  @Test
  void testFileUpload_shouldReturnDocumentDetails() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "dummy.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)));

    CreateDocumentDto data = CreateDocumentDto
        .builder()
        .name("Résumé TG")
        .shared(false)
        .tags(createdTagsIds)
        .build();

    MockMultipartFile jsonData = new MockMultipartFile(
        "data",
        null,
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(data));

    mockMvc
        .perform(
            multipart("/documents")
                .file(file)
                .file(jsonData)
                .with(user(testUser)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(data.getName()))
        .andExpect(jsonPath("$.shared").value(data.getShared()))
        .andExpect(jsonPath("$.tags").value(Matchers.hasSize(data.getTags().size())))
        .andExpect(jsonPath("$.tags").value(Matchers.containsInAnyOrder(data.getTags())));
  }

  @Test
  void testFileListing_shoudReturnListOfUploadedDocuments() throws Exception {
    documentsRepository.saveAll(
        List.of(
            Document
                .builder()
                .name("Résumé TG")
                .shared(false)
                .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
                .build(),
            Document
                .builder()
                .name("Java Cheatsheet (LOL)")
                .shared(false)
                .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
                .build()));

    mockMvc
        .perform(get("/documents")
            .with(user(testUser)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length").value(2));
  }

  @Test
  void testGetDocumentDetails_shoudReturnDocumentDetails() throws Exception {
    Document doc = documentsRepository.save(
        Document
            .builder()
            .name("Résumé TG")
            .shared(false)
            .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
            .build());

    mockMvc
        .perform(
            get(String.format("/documents/%d", doc.getId()))
                .with(user(testUser)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(doc.getName()))
        .andExpect(jsonPath("$.shared").value(doc.isShared()))
        .andExpect(jsonPath("$.tags").value(Matchers.hasSize(doc.getTags().size())))
        .andExpect(jsonPath("$.tags").value(Matchers.containsInAnyOrder(doc.getTags())));
  }

  @Test
  void testGetDocumentDetails_givenWrongId_shoudReturnNotFound() throws Exception {
    Document doc = documentsRepository.save(
        Document
            .builder()
            .name("Résumé TG")
            .shared(false)
            .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
            .build());

    mockMvc
        .perform(
            get(String.format("/documents/%d", doc.getId() + 1))
                .with(user(testUser)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").isString());
  }

  @Test
  void testDocumentEditDetails_shouldReturnNewDocumentDetails() throws Exception {
    Document doc = documentsRepository.save(
        Document
            .builder()
            .name("Résumé TG")
            .shared(false)
            .file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
            .build());

    EditDocumentDto editData = EditDocumentDto
        .builder()
        .name("Résumé Théorie des Graphes")
        .shared(true)
        .build();

    mockMvc
        .perform(
            patch(String.format("/documents/%d", doc.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editData))
                .with(user(testUser)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(editData.getName()))
        .andExpect(jsonPath("$.shared").value(editData.isShared()));

    Document newDoc = documentsRepository.findById(doc.getId()).orElseThrow();
    assertEquals(editData.getName(), newDoc.getName());
    assertEquals(editData.isShared(), newDoc.isShared());
  }

}
