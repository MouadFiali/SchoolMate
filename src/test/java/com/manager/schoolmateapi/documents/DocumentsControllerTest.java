package com.manager.schoolmateapi.documents;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.manager.schoolmateapi.SchoolMateApiApplication;
import com.manager.schoolmateapi.documents.dto.CreateDocumentDto;
import com.manager.schoolmateapi.documents.dto.CreateDocumentTagDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentDto;
import com.manager.schoolmateapi.documents.dto.EditDocumentTagDto;
import com.manager.schoolmateapi.documents.models.Document;
import com.manager.schoolmateapi.documents.models.DocumentTag;
import com.manager.schoolmateapi.documents.repositories.DocumentTagsRepository;
import com.manager.schoolmateapi.documents.repositories.DocumentsRepository;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;
import com.manager.schoolmateapi.utils.dto.PaginatedResponse;

import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SchoolMateApiApplication.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
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
	MyUserDetails anotherTestUser;

	List<Long> userCreatedTagsIds;
	List<Long> anotherUserCreatedTagsIds;

	@BeforeAll
	public void setup() {
		// Create test users
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Smith");
		user.setRole(UserRole.STUDENT);
		user.setPassword("password");
		user.setEmail("john.smith@gmail.com");

		User anotherUser = new User();
		anotherUser.setFirstName("Jane");
		anotherUser.setLastName("Doe");
		anotherUser.setEmail("jane.doe@gmail.com");
		anotherUser.setPassword("password");
		anotherUser.setRole(UserRole.STUDENT);
		anotherUser.setActive(true);

		// Save the test users
		User newUser = userRepository.save(user);
		testUser = new MyUserDetails(newUser);

		User anotherNewUser = userRepository.save(anotherUser);
		anotherTestUser = new MyUserDetails(anotherNewUser);

		// Create some tags
		Iterable<DocumentTag> tags = documentTagsRepository.saveAll(
				List.of(
						DocumentTag.builder().name("Théorie des graphes").user(testUser.getUser()).build(),
						DocumentTag.builder().name("Compilation").user(testUser.getUser()).build(),
						DocumentTag.builder().name("1ère année").user(testUser.getUser()).build(),
						DocumentTag.builder().name("2ème année").user(testUser.getUser()).build()));

		userCreatedTagsIds = StreamSupport.stream(tags.spliterator(), false)
				.map(tag -> tag.getId()).toList();

		Iterable<DocumentTag> otherTags = documentTagsRepository.saveAll(
				List.of(
						DocumentTag.builder().name("Programmation Linéaire").user(anotherTestUser.getUser()).build(),
						DocumentTag.builder().name("Théorie des langages").user(anotherTestUser.getUser()).build(),
						DocumentTag.builder().name("3ème année").user(anotherTestUser.getUser()).build(),
						DocumentTag.builder().name("PFE").user(anotherTestUser.getUser()).build()));

		anotherUserCreatedTagsIds = StreamSupport.stream(otherTags.spliterator(), false)
				.map(tag -> tag.getId()).toList();
	}

	@Test
	public void testFileUpload_shouldReturnDocumentDetails() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"dummy.pdf",
				MediaType.APPLICATION_PDF_VALUE,
				Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)));

		CreateDocumentDto data = CreateDocumentDto
				.builder()
				.name("Résumé TG")
				.shared(false)
				.tags(userCreatedTagsIds)
				.build();

		MockMultipartFile jsonData = new MockMultipartFile(
				"data",
				null,
				MediaType.APPLICATION_JSON_VALUE,
				objectMapper.writeValueAsBytes(data));

		MvcResult result = mockMvc
				.perform(
						multipart("/documents")
								.file(file)
								.file(jsonData)
								.with(user(testUser)))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.id").value(Matchers.anyOf(Matchers.instanceOf(Integer.class), Matchers.instanceOf(long.class))))
				.andExpect(jsonPath("$.name").value(data.getName()))
				.andExpect(jsonPath("$.shared").value(data.isShared()))
				.andExpect(jsonPath("$.tags").value(Matchers.hasSize(data.getTags().size())))
				.andExpect(jsonPath(
						"$.tags[*].id",
						Matchers.containsInAnyOrder(userCreatedTagsIds.toArray()),
						Long.class).exists())
				.andReturn();

		long id = ((Number) JsonPath.parse(result.getResponse().getContentAsString()).read("$.id")).longValue();
		Optional<Document> newDoc = documentsRepository.findById(id);
		assertTrue(newDoc.isPresent());
		assertNotNull(newDoc.get().getUser());
		assertEquals(newDoc.get().getUser().getId(), testUser.getUser().getId());
		assertEquals(newDoc.get().getName(), data.getName());
		assertEquals(newDoc.get().isShared(), data.isShared());
		assertEquals(newDoc.get().getTags().size(), data.getTags().size());
		assertThat(
				newDoc.get().getTags().stream().map(tag -> tag.getId()).toList(),
				Matchers.containsInAnyOrder(userCreatedTagsIds.toArray()));
	}

	@Test
	public void testFileUpload_givenWrongTagIds_shouldReturnNotFound() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"dummy.pdf",
				MediaType.APPLICATION_PDF_VALUE,
				Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)));

		List<Long> testingTags = List.of(userCreatedTagsIds.get(0) + 100, anotherUserCreatedTagsIds.get(0));

		testingTags.forEach(tag -> {
			CreateDocumentDto data = CreateDocumentDto
					.builder()
					.name("Résumé TG")
					.shared(false)
					.tags(List.of(tag))
					.build();

			try {
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
						.andExpect(status().isNotFound())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Test
	public void testFileListing_shoudReturnListOfUploadedDocuments() throws Exception {
		documentsRepository.saveAll(
				List.of(
						Document
								.builder()
								.name("Résumé TG")
								.shared(false)
								.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
								.user(testUser.getUser())
								.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
								.build(),
						Document
								.builder()
								.name("Java Cheatsheet (LOL)")
								.shared(false)
								.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
								.user(testUser.getUser())
								.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
								.build()));

		String response = mockMvc
				.perform(get("/documents")
						.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.results").value(Matchers.hasSize(2)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		objectMapper.readValue(response, PaginatedResponse.class);
	}

	@Test
	public void testFileListing_withLotsOfItems_shoudReturnListOfUploadedDocuments() throws Exception {
		List<Document> listOfDocs = new ArrayList<>();

		int pageSize = 30;
		int page = 2;

		for (int index = 0; index < 100; index++) {
			listOfDocs.add(
					Document
							.builder()
							.name(String.format("Resource Number %d", index + 1))
							.shared(false)
							.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
							.user(testUser.getUser())
							.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
							.build());
		}

		documentsRepository.saveAll(listOfDocs);

		String response = mockMvc
				.perform(get("/documents")
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		@SuppressWarnings("unchecked")
		PaginatedResponse<Document> paginatedResponse = (PaginatedResponse<Document>) objectMapper.readValue(response,
				PaginatedResponse.class);

		assertEquals(paginatedResponse.getResults().size(), paginatedResponse.getCount());
		assertEquals(paginatedResponse.getPage(), page);
		assertEquals(paginatedResponse.getTotalItems(), listOfDocs.size());
		assertEquals(paginatedResponse.getCount(), pageSize);
		assertFalse(paginatedResponse.isLast());
	}

	@Test
	public void testFileListing_givenTagFilter_shouldReturnFilteredListOfDocuments() throws Exception {
		List<DocumentTag> createdTags = documentTagsRepository.findAllById(userCreatedTagsIds);
		documentsRepository.saveAll(
				List.of(
						Document
								.builder()
								.name("Résumé TG")
								.shared(false)
								.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
								.user(testUser.getUser())
								.tags(createdTags.subList(0, 2).stream()
										.collect(Collectors.toSet()))
								.build(),
						Document
								.builder()
								.name("Java Cheatsheet (LOL)")
								.shared(true)
								.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
								.user(testUser.getUser())
								.tags(createdTags.subList(2, 4).stream()
										.collect(Collectors.toSet()))
								.build()));

		String response = mockMvc
				.perform(get("/documents")
						.queryParam("tags", String.valueOf(createdTags.get(2).getId()))
						.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.results").value(Matchers.hasSize(1)))
				.andExpect(jsonPath("$.results[0].name").value("Java Cheatsheet (LOL)"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		objectMapper.readValue(response, PaginatedResponse.class);
	}

	@Test
	public void testFileListingOfOtherUsers_shouldReturnDocumentsOfOtherUsers() throws Exception {
		documentsRepository.saveAll(
			List.of(
				Document
					.builder()
					.name("Shared resource for others")
					.shared(true)
					.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
					.user(anotherTestUser.getUser())
					.build(),
				Document
					.builder()
					.name("Not shared resource for others")
					.shared(false)
					.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
					.user(anotherTestUser.getUser())
					.build(),
				Document
					.builder()
					.name("Out of context")
					.shared(false)
					.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
					.user(testUser.getUser())
					.build()
			)
		);

		String response = mockMvc
				.perform(get("/documents/user/{id}", anotherTestUser.getUser().getId())
						.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.results").value(Matchers.hasSize(1)))
				.andExpect(jsonPath("$.results[0].name").value("Shared resource for others"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		objectMapper.readValue(response, PaginatedResponse.class);
	}

	@Test
	public void testGetDocumentDetails_shoudReturnDocumentDetails() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("Résumé TG")
						.shared(false)
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.user(testUser.getUser())
						.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
						.build());

		mockMvc
				.perform(
						get(String.format("/documents/%d", doc.getId()))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value(doc.getName()))
				.andExpect(jsonPath("$.shared").value(doc.isShared()))
				.andExpect(jsonPath("$.tags").value(Matchers.hasSize(userCreatedTagsIds.size())))
				.andExpect(jsonPath(
						"$.tags[*].id",
						Matchers.containsInAnyOrder(userCreatedTagsIds.toArray()),
						Long.class).exists());
	}

	@Test
	public void testGetDocumentDetails_givenWrongId_shoudReturnNotFound() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("Résumé TG")
						.shared(false)
						.user(testUser.getUser())
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
						.build());

		mockMvc
				.perform(
						get(String.format("/documents/%d", doc.getId() + 1))
								.with(user(testUser)))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}

	@Test
	public void testDocumentEditDetails_shouldReturnNewDocumentDetails() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("Résumé TG")
						.shared(false)
						.user(testUser.getUser())
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
						.build());

		List<Long> newTagsIds = userCreatedTagsIds.subList(0, 2);

		EditDocumentDto editData = EditDocumentDto
				.builder()
				.name("Résumé Théorie des Graphes")
				.shared(true)
				.tags(newTagsIds)
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
				.andExpect(jsonPath("$.shared").value(editData.isShared()))
				.andExpect(jsonPath("$.tags").value(Matchers.hasSize(newTagsIds.size())))
				.andExpect(jsonPath(
						"$.tags[*].id",
						Matchers.containsInAnyOrder(newTagsIds.toArray()),
						Long.class).exists());

		Document newDoc = documentsRepository.findById(doc.getId()).orElseThrow();
		assertEquals(editData.getName(), newDoc.getName());
		assertEquals(editData.isShared(), newDoc.isShared());
		assertThat(newDoc.getTags().stream().map(tag -> tag.getId()).toList(),
				Matchers.containsInAnyOrder(newTagsIds.toArray()));
	}

	@Test
	public void testDeleteDocument_shoudReturnSuccessMessage() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("PostgreSQL Cheatsheet")
						.shared(true)
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
						.build());

		mockMvc
				.perform(
						delete(String.format("/documents/%d", doc.getId()))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").isString());

		Optional<Document> document = documentsRepository.findById(doc.getId());
		assertTrue(document.isEmpty());
	}

	@Test
	public void testDocumentDownload_shoudReturnFileBytes() throws Exception {
		Document doc = documentsRepository.save(
				Document
						.builder()
						.name("PostgreSQL Cheatsheet")
						.shared(true)
						.file(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH)))
						.tags(documentTagsRepository.findAllById(userCreatedTagsIds).stream().collect(Collectors.toSet()))
						.user(testUser.getUser())
						.build());

		mockMvc
				.perform(
						get(String.format("/documents/%d/file", doc.getId()))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(content().bytes(Files.readAllBytes(Paths.get(DUMMY_PDF_PATH))));
	}

	@Test
	public void testAddTag_shouldReturnNewTag() throws Exception {
		CreateDocumentTagDto cTagDto = CreateDocumentTagDto.builder().name("PFA").build();

		MvcResult result = mockMvc.perform(
				post("/documents/tags")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(cTagDto))
						.with(user(testUser)))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.id").value(Matchers.anyOf(Matchers.instanceOf(Integer.class), Matchers.instanceOf(long.class))))
				.andExpect(jsonPath("$.name").value(cTagDto.getName()))
				.andExpect(jsonPath("$.createdAt").isString())
				.andReturn();

		long id = ((Number) JsonPath.parse(result.getResponse().getContentAsString()).read("$.id")).longValue();
		Optional<DocumentTag> newTag = documentTagsRepository.findById(id);
		assertTrue(newTag.isPresent());
		assertNotNull(newTag.get().getUser());
		assertEquals(newTag.get().getUser().getId(), testUser.getUser().getId());
	}

	@Test
	public void testListTags_shouldReturnTags() throws Exception {
		List<DocumentTag> listOfUserTags = documentTagsRepository.findAllById(userCreatedTagsIds);

		mockMvc
				.perform(
						get("/documents/tags")
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[*]").value(Matchers.hasSize(listOfUserTags.size())))
				.andExpect(jsonPath("$.[*].name").value(
						Matchers.containsInAnyOrder(listOfUserTags.stream().map(tag -> tag.getName()).toArray())))
				.andExpect(jsonPath("$.[*].createdAt").value(
						Matchers.everyItem(Matchers.instanceOf(String.class))));
	}

	@Test
	public void testEditTag_shouldReturnNewTag() throws Exception {
		DocumentTag docTag = documentTagsRepository.save(
				DocumentTag.builder()
						.name("DevOps")
						.user(testUser.getUser())
						.build());

		EditDocumentTagDto eTagDto = EditDocumentTagDto.builder().name("DevOps Resources").build();

		mockMvc
				.perform(
						patch(String.format("/documents/tags/%d", docTag.getId()))
								.with(user(testUser))
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(eTagDto)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(docTag.getId()))
				.andExpect(jsonPath("$.name").value(eTagDto.getName()))
				.andExpect(jsonPath("$.createdAt").isString());

		Optional<DocumentTag> newDocTag = documentTagsRepository.findById(docTag.getId());
		assertTrue(newDocTag.isPresent());
		assertEquals(newDocTag.get().getName(), eTagDto.getName());
	}

	@Test
	public void testEditTag_givenWrongId_shouldReturnNotFound() throws Exception {
		DocumentTag docTag = documentTagsRepository.save(
				DocumentTag.builder()
						.name("DevOps")
						.user(testUser.getUser())
						.build());

		EditDocumentTagDto eTagDto = EditDocumentTagDto.builder().name("DevOps Resources").build();

		mockMvc
				.perform(
						patch(String.format("/documents/tags/%d", docTag.getId() + 1))
								.with(user(testUser))
								.content(objectMapper.writeValueAsString(eTagDto))
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}

	@Test
	public void testDeleteTag_shouldReturnSuccessMessage() throws Exception {
		DocumentTag docTag = documentTagsRepository.save(
				DocumentTag.builder()
						.name("Test Driven Development :(")
						.user(testUser.getUser())
						.build());

		mockMvc
				.perform(
						delete(String.format("/documents/tags/%d", docTag.getId()))
								.with(user(testUser)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").isString());

		Optional<DocumentTag> deletedDocTag = documentTagsRepository.findById(docTag.getId());
		assertTrue(deletedDocTag.isEmpty());
	}

	@Test
	public void testDeleteTag_givenWrongId_shouldReturnSuccessMessage() throws Exception {
		DocumentTag docTag = documentTagsRepository.save(
				DocumentTag.builder()
						.name("Test Driven Development :(")
						.user(testUser.getUser())
						.build());

		mockMvc
				.perform(
						delete(String.format("/documents/tags/%d", docTag.getId() + 1))
								.with(user(testUser)))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}

}
