package com.manager.schoolmateapi.complaints;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.manager.schoolmateapi.SchoolMateApiApplication;
import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusAndHandlerDto;
import com.manager.schoolmateapi.complaints.enumerations.BuildingProb;
import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;
import com.manager.schoolmateapi.complaints.enumerations.FacilityType;
import com.manager.schoolmateapi.complaints.enumerations.RoomProb;
import com.manager.schoolmateapi.complaints.models.BuildingComplaint;
import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;
import com.manager.schoolmateapi.complaints.models.RoomComplaint;
import com.manager.schoolmateapi.complaints.repositories.BuildingComplaintRepo;
import com.manager.schoolmateapi.complaints.repositories.FacilitiesComplaintRepo;
import com.manager.schoolmateapi.complaints.repositories.RoomComplaintRepo;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.MyUserDetails;
import com.manager.schoolmateapi.users.models.User;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SchoolMateApiApplication.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ComplaintsControllerTest {
    @Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
    BuildingComplaintRepo buildingComplaintRepo;

    @Autowired
    FacilitiesComplaintRepo facilitiesComplaintRepo;

    @Autowired
    RoomComplaintRepo roomComplaintRepo;

	@Autowired
	UserRepository userRepository;

	MyUserDetails complainant;
	MyUserDetails handler;
	MyUserDetails complainant2;


    @BeforeAll
	@Transactional
	void setup() {
		// Create a test user (complainant)
		User userComplainant = new User();
		userComplainant.setFirstName("John");
		userComplainant.setLastName("Smith");
		userComplainant.setRole(UserRole.STUDENT);
		userComplainant.setPassword("password");
		userComplainant.setEmail("john.smith@gmail.com");

		// Create a test user 2 (complaint handler)
		User userHandler = new User();
		userHandler.setFirstName("Jane");
		userHandler.setLastName("Doe");
		userHandler.setRole(UserRole.ADEI);
		userHandler.setPassword("password");
		userHandler.setEmail("jane.doe@gmail.com");

		// Create another test user (complainant 2)
		User userComplainant2 = new User();
		userComplainant2.setFirstName("Mike");
		userComplainant2.setLastName("Ross");
		userComplainant2.setRole(UserRole.STUDENT);
		userComplainant2.setPassword("password");
		userComplainant2.setEmail("mike.ross@gmail.com");

		// Save the test users
		userComplainant = userRepository.save(userComplainant);
		userHandler = userRepository.save(userHandler);
		userComplainant2 = userRepository.save(userComplainant2);

		complainant = new MyUserDetails(userComplainant);
		handler = new MyUserDetails(userHandler);
		complainant2 = new MyUserDetails(userComplainant2);
		
		// Create some test complaints
		// Building complaint
		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("A");
		buildingComp.setBuildingProb(BuildingProb.ELECTRICITY);
		buildingComp.setDescription("The building does not have electricity");
		buildingComp.setComplainant(complainant.getUser());
		buildingComp.setStatus(ComplaintStatus.PENDING);
		buildingComp.setDate(new Date());

		// Room complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("C36");
		roomComp.setRoomProb(RoomProb.OTHER);
		roomComp.setDescription("The room does not have a window");
		roomComp.setComplainant(complainant.getUser());
		roomComp.setHandler(handler.getUser());
		roomComp.setStatus(ComplaintStatus.ASSIGNED);
		roomComp.setDate(new Date());

		// Facility complaint
		FacilitiesComplaint facilityComp = new FacilitiesComplaint();
		facilityComp.setFacilityType(FacilityType.PLAYGROUND);
		facilityComp.setDescription("The basketball court does not have a net");
		facilityComp.setComplainant(complainant.getUser());
		facilityComp.setHandler(handler.getUser());
		facilityComp.setStatus(ComplaintStatus.RESOLVING);
		facilityComp.setDate(new Date());

		//Facility complaint 2 (claimed by complainant 2)
		FacilitiesComplaint facilityComp2 = new FacilitiesComplaint();
		facilityComp2.setFacilityType(FacilityType.CLASS);
		facilityComp2.setClassName("Amphi 3");
		facilityComp2.setDescription("The classroom does not have a projector");
		facilityComp2.setComplainant(complainant2.getUser());
		facilityComp2.setHandler(handler.getUser());
		facilityComp2.setStatus(ComplaintStatus.RESOLVING);
		facilityComp2.setDate(new Date());

		// Save the test complaints
		buildingComplaintRepo.save(buildingComp);
		roomComplaintRepo.save(roomComp);
		facilitiesComplaintRepo.save(facilityComp);
		facilitiesComplaintRepo.save(facilityComp2);

	}


	//Test create complaints with special cases -----------------------------------
    @Test //Test create building complaint with all required fields
	public void testCreateBuildingComplaint_shouldReturnCreatedComplaint() throws Exception {

		CreateBuildingComplaintDto dto = CreateBuildingComplaintDto.builder()
			.building("B")
			.buildingProb(BuildingProb.SHOWER)
			.description("The building does not have hot water")
			.build();

		String response = mockMvc.perform(post("/complaints")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isCreated())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.building").value("B"))
						.andExpect(jsonPath("$.buildingProb").value("SHOWER"))
						.andExpect(jsonPath("$.handler").value(IsNull.nullValue()))
						.andExpect(jsonPath("$.complainant.lastName").value("Smith"))
						.andExpect(jsonPath("$.status").value("PENDING"))
						.andExpect(jsonPath("$.dtype").value("BuildingComplaint")) // check that the dtype is not null and is set to BuildingComplaint
						.andExpect(jsonPath("$.description").value("The building does not have hot water"))
						.andReturn().getResponse().getContentAsString();
		
	
		// Delete the complaint from the database after the test 
		long id = ((Number) JsonPath.parse(response).read("$.id")).longValue();
		buildingComplaintRepo.deleteById(id);
	}

	@Test //Test create facility complaint with missing facility field
	public void testCreateFacilitiesComplaint_shouldReturnFacilityRequired() throws Exception {
		CreateFacilityComplaintDto dto = CreateFacilityComplaintDto.builder()
			.description("The wifi is not working")
			.build();

		mockMvc.perform(post("/complaints")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.errors").value("The facility type is required"))
						.andReturn();
	}

	@Test //Test create facility complaint with missing class field when facility type is class
	public void testCreateFacilitiesComplaint_classComplaint_shouldReturnClassRequired() throws Exception {
		CreateFacilityComplaintDto dto = CreateFacilityComplaintDto.builder()
			.facilityType(FacilityType.CLASS)
			.description("The class is closed very early")
			.build();

		mockMvc.perform(post("/complaints")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.errors").value("The class name should not be empty if the facility type is a class"))
						.andReturn();
	}
	//End test create complaints by type -------------------------------

	//Test get complaint by type----------------------------------------
	@Test //Test get building complaints of all users
	public void testGetBuildingComplaints_shouldReturnAllBuildingComps() throws Exception {
		int pageSize = 1;
		int page = 0;

		mockMvc.perform(get("/complaints?type=building")
						.with(user(complainant))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
						.andExpect(jsonPath("$.results[0].building").value("A"))
						.andExpect(jsonPath("$.results[0].buildingProb").value("ELECTRICITY"))
						.andExpect(jsonPath("$.results[0].handler").value(IsNull.nullValue()))
						.andExpect(jsonPath("$.results[0].status").value("PENDING"))
						.andReturn();
	}

	@Test //Test get facilities complaints of a specific user
	public void testGetFacilitiesComplaints_shouldReturnFacilitiesCompsForUser() throws Exception {
		int pageSize = 1;
		int page = 0;

		mockMvc.perform(get("/complaints?type=facilities&user=me")
						.with(user(complainant))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
						.andExpect(jsonPath("$.results[0].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$.results[0].handler.lastName").value("Doe"))
						.andExpect(jsonPath("$.results[0].complainant.lastName").value("Smith"))
						.andExpect(jsonPath("$.results[0].status").value("RESOLVING"))
						.andReturn();
	}

	@Test //Test get facilities complaints of all users
	public void testGetFacilitiesComplaints_shouldReturnFacilitiesCompsForAll() throws Exception {
		int pageSize = 1;
		int page = 0;

		mockMvc.perform(get("/complaints?type=facilities&user=all")
						.with(user(complainant))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
						// We have 2 complaints, every page has 1 complaint as we set the page size to 1 -> so we have 2 pages
						.andExpect(jsonPath("$.totalPages").value(2))
						//First complaint
						.andExpect(jsonPath("$.results[0].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$.results[0].handler.lastName").value("Doe"))
						.andExpect(jsonPath("$.results[0].complainant.lastName").value("Smith"))
						.andReturn();
		
		// get second page
		page = 1;
		mockMvc.perform(get("/complaints?type=facilities&user=all")
						.with(user(complainant))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
						//Second complaint
						.andExpect(jsonPath("$.results[0].facilityType").value("CLASS"))
						.andExpect(jsonPath("$.results[0].handler.lastName").value("Doe"))
						.andExpect(jsonPath("$.results[0].complainant.lastName").value("Ross"))
						.andExpect(jsonPath("$.results[0].status").value("CONFIRMED"))
						.andReturn();
	}

	@Test //Test get all complaints of a specific user
	public void testGetAllComplaints_shouldReturnAllComplaintsForUser() throws Exception{
		int pageSize = 3; // We have 3 complaints
		int page = 0;

		mockMvc.perform(get("/complaints?user=me") // or /complaints?type=all&user=me
						.with(user(complainant)) //John Smith
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
						//First complaint
						.andExpect(jsonPath("$.results[0].building").value("A"))
						.andExpect(jsonPath("$.results[0].complainant.lastName").value("Smith"))
						//Second complaint
						.andExpect(jsonPath("$.results[1].room").value("C36"))
						.andExpect(jsonPath("$.results[1].complainant.lastName").value("Smith"))
						//Third complaint
						.andExpect(jsonPath("$.results[2].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$.results[2].complainant.lastName").value("Smith"))
						.andReturn();
	}

	
	@Test //Test get all complaints of all users
	public void testGetAllComplaints_shouldReturnAllComplaintsForAll() throws Exception{
		int pageSize = 4; // We have 4 complaints
		int page = 0;

		mockMvc.perform(get("/complaints") // or /complaints?type=all?user=all or /complaints?type=all
						.with(user(complainant)) //John Smith
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.results").value(Matchers.hasSize(pageSize)))
						//First complaint
						.andExpect(jsonPath("$.results[0].building").value("A"))
						.andExpect(jsonPath("$.results[0].complainant.lastName").value("Smith"))
						//Second complaint
						.andExpect(jsonPath("$.results[1].room").value("C36"))
						.andExpect(jsonPath("$.results[1].complainant.lastName").value("Smith"))
						//Third complaint
						.andExpect(jsonPath("$.results[2].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$.results[2].complainant.lastName").value("Smith"))
						//Fourth complaint
						.andExpect(jsonPath("$.results[3].facilityType").value("CLASS"))
						.andExpect(jsonPath("$.results[3].complainant.lastName").value("Ross"))
						.andReturn();
	}
	//End test get complaint by type------------------------------------

	//Test get complaint by id------------------------------------------
	@Test
	public void testGetComplaintById() throws Exception {
		//create a new room complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setStatus(ComplaintStatus.PENDING);
		roomComp.setDate(new Date());
		roomComp.setDescription("The water is not working");

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		//get the complaint by id
		mockMvc.perform(get("/complaints/" + roomComp.getId())
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.room").value("D15"))
						.andExpect(jsonPath("$.roomProb").value("WATER"))
						.andExpect(jsonPath("$.complainant.lastName").value("Smith"))
						.andExpect(jsonPath("$.status").value("PENDING"))
						.andReturn();
		
		//delete the complaint
		roomComplaintRepo.delete(roomComp);
	}

	@Test
	public void testGetComplaintById_notFound() throws Exception {
		mockMvc.perform(get("/complaints/999999999")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isNotFound())
						.andReturn();
	}

	//test get complaints of a specific user------------------------------
	@Test //get specific user's room complaints
	public void testGetRoomComplaintOfUser_shouldReturnComplaintsForSpecificUser() throws Exception {

		// create more complaints for complainant 2
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant2.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setStatus(ComplaintStatus.PENDING);

		RoomComplaint roomComp2 = new RoomComplaint();
		roomComp2.setRoom("C8");
		roomComp2.setRoomProb(RoomProb.ELECTRICITY);
		roomComp2.setComplainant(complainant2.getUser());
		roomComp2.setDescription("The electricity is not working");
		roomComp2.setDate(new Date());
		roomComp2.setStatus(ComplaintStatus.PENDING);

		//save the complaints
		roomComp = roomComplaintRepo.save(roomComp);
		roomComp2 = roomComplaintRepo.save(roomComp2);

		int pageSize = 5;
		int page = 0;

		//get the complaints of complainant 2
		mockMvc.perform(get("/complaints?type=room&user=" + complainant2.getUser().getId())
						.with(user(complainant))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						//Check if the size of the results is less than the page size (We are not sure how many complaints the user has for example)
						.andExpect(jsonPath("$.results", Matchers.hasSize(Matchers.lessThan(pageSize))))
						//Check if all the complaints has the same user Ross
						.andExpect(jsonPath("$.results[*].complainant.lastName", Matchers.everyItem(Matchers.is("Ross"))))
						.andReturn();

		//delete the complaints
		roomComplaintRepo.deleteById(roomComp.getId());
		roomComplaintRepo.deleteById(roomComp2.getId());

	}

	@Test //get all user's complaints
	public void testGetAllComplaintsOfUser_shouldReturnComplaintsForSpecificUser() throws Exception {
		//Create complaints for complainant 2
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant2.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setStatus(ComplaintStatus.PENDING);

		RoomComplaint roomComp2 = new RoomComplaint();
		roomComp2.setRoom("C8");
		roomComp2.setRoomProb(RoomProb.ELECTRICITY);
		roomComp2.setComplainant(complainant2.getUser());
		roomComp2.setDescription("The electricity is not working");
		roomComp2.setDate(new Date());
		roomComp2.setStatus(ComplaintStatus.PENDING);

		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setComplainant(complainant2.getUser());
		buildingComp.setDescription("The shower is not working");
		buildingComp.setDate(new Date());
		buildingComp.setStatus(ComplaintStatus.PENDING);

		//save the complaints
		roomComp = roomComplaintRepo.save(roomComp);
		roomComp2 = roomComplaintRepo.save(roomComp2);
		buildingComp = buildingComplaintRepo.save(buildingComp);

		int pageSize = 4;
		int page = 0;

		//get the complaints of complainant 2
		mockMvc.perform(get("/complaints?user=" + complainant2.getUser().getId())
						.with(user(complainant))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						// 2 room complaints, 1 building complaint and 1 facility complaint = 4
						.andExpect(jsonPath("$.results", Matchers.hasSize(pageSize)))
						//Check if all the complaints has the same user Ross
						.andExpect(jsonPath("$.results[*].complainant.lastName", Matchers.everyItem(Matchers.is("Ross"))))
						.andReturn();

		//delete the complaints
		roomComplaintRepo.deleteById(roomComp.getId());
		roomComplaintRepo.deleteById(roomComp2.getId());
		buildingComplaintRepo.deleteById(buildingComp.getId());

	}

	@Test //try to get complaints of a specific user with invalid user id (not a number)
	public void testGetComplaintOfUserWithInvalidUserId_shouldReturnBadRequest() throws Exception {
		//get the complaints of complainant 2
		mockMvc.perform(get("/complaints?type=room&user=invalid")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
						.andExpect(jsonPath("$.detail", Matchers.is("Invalid user parameter")))
						.andReturn();
	}

	//End test get complaint of a specific user--------------------------

	//Test get complaint by status and user specs---------------------------------------------
	@Test //get all complaints of a specific user (handler) with a specific status
	public void testGetComplaintByStatusAndWrongUser_shouldReturnWrongUser() throws Exception {
		int pageSize = 1;
		int page = 0;

		//get the complaints of complainant
		mockMvc.perform(get("/complaints-by-status?user=" + complainant.getUser().getId() + "&status=" + ComplaintStatus.RESOLVING)
						.with(user(handler))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
						.andExpect(jsonPath("$.detail", Matchers.is("User is not an ADEI member")))
						.andReturn();
	}

	@Test //get all pending complaints of a specific type
	public void testGetBuildingComplaintsByStatusPENDING_shouldReturnBuildingComplaints() throws Exception {
		// create a complaint (PENDING)
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setStatus(ComplaintStatus.PENDING);

		// create a complaint (PENDING)
		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setComplainant(complainant.getUser());
		buildingComp.setDescription("The shower is not working");
		buildingComp.setDate(new Date());
		buildingComp.setStatus(ComplaintStatus.PENDING);

		// create a complaint (PENDING)
		BuildingComplaint buildingComp2 = new BuildingComplaint();
		buildingComp2.setBuilding("F");
		buildingComp2.setBuildingProb(BuildingProb.ELECTRICITY);
		buildingComp2.setComplainant(complainant.getUser());
		buildingComp2.setDescription("The elec is not working");
		buildingComp2.setDate(new Date());
		buildingComp2.setStatus(ComplaintStatus.PENDING);

		// create a complaint (PENDING)
		FacilitiesComplaint facilityComp2 = new FacilitiesComplaint();
		facilityComp2.setFacilityType(FacilityType.CLASS);
		facilityComp2.setClassName("Amphi 5");
		facilityComp2.setDescription("The classroom is dead");
		facilityComp2.setComplainant(complainant.getUser());
		facilityComp2.setStatus(ComplaintStatus.PENDING);
		facilityComp2.setDate(new Date());

		//save the complaints
		roomComp = roomComplaintRepo.save(roomComp);
		buildingComp = buildingComplaintRepo.save(buildingComp);
		buildingComp2 = buildingComplaintRepo.save(buildingComp2);
		facilityComp2 = facilitiesComplaintRepo.save(facilityComp2);

		int pageSize = 3;
		int page = 0;

		//get the complaints of complainant
		mockMvc.perform(get("/complaints-by-status?status=" + ComplaintStatus.PENDING + "&type=building")
						.with(user(handler))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						// 2 building complaints with status PENDING
						.andExpect(jsonPath("$.results", Matchers.hasSize(pageSize)))
						//Check if all the complaints has the same user Ross
						.andExpect(jsonPath("$.results[*].complainant.lastName", Matchers.everyItem(Matchers.is("Smith"))))
						// Check if all the complaints has the same status PENDING
						.andExpect(jsonPath("$.results[*].status", Matchers.everyItem(Matchers.is(ComplaintStatus.PENDING.toString()))))
						// check the type of the complaints
						.andExpect(jsonPath("$.results[*].dtype", Matchers.everyItem(Matchers.is("BuildingComplaint"))))
						.andReturn();

		// delete the complaints
		roomComplaintRepo.delete(roomComp);
		buildingComplaintRepo.delete(buildingComp);
		buildingComplaintRepo.delete(buildingComp2);
		facilitiesComplaintRepo.delete(facilityComp2);
	}

	@Test //get all resolving complaints of a specific type
	public void testGetBuildingComplaintsByStatusRESOLVING_shouldReturnBuildingComplaints() throws Exception {
		// create a complaint 1 with handler 1 (RESOLVING)
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setHandler(handler.getUser());
		roomComp.setStatus(ComplaintStatus.RESOLVING);

		// create a complaint 2 with handler 1 (RESOLVING)
		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setComplainant(complainant.getUser());
		buildingComp.setDescription("The shower is not working");
		buildingComp.setDate(new Date());
		buildingComp.setHandler(handler.getUser());
		buildingComp.setStatus(ComplaintStatus.RESOLVING);

		// Create another handler
		User handler2 = new User();
		handler2.setFirstName("handler");
		handler2.setLastName("handler");
		handler2.setEmail("handler@um5.ac.ma");
		handler2.setPassword("handler");
		handler2.setRole(UserRole.ADEI);

		handler2 = userRepository.save(handler2);

		// create a complaint 1 with handler 2 (RESOLVING)
		BuildingComplaint buildingComp2 = new BuildingComplaint();
		buildingComp2.setBuilding("F");
		buildingComp2.setBuildingProb(BuildingProb.ELECTRICITY);
		buildingComp2.setComplainant(complainant.getUser());
		buildingComp2.setDescription("The elec is not working");
		buildingComp2.setDate(new Date());
		buildingComp2.setHandler(handler2);
		buildingComp2.setStatus(ComplaintStatus.RESOLVING);

		// create a complaint 3 with handler 1 (RESOLVING)
		FacilitiesComplaint facilityComp2 = new FacilitiesComplaint();
		facilityComp2.setFacilityType(FacilityType.CLASS);
		facilityComp2.setClassName("Amphi 5");
		facilityComp2.setDescription("The classroom is dead");
		facilityComp2.setComplainant(complainant.getUser());
		facilityComp2.setStatus(ComplaintStatus.RESOLVING);
		facilityComp2.setHandler(handler.getUser());
		facilityComp2.setDate(new Date());

		//save the complaints
		roomComp = roomComplaintRepo.save(roomComp);
		buildingComp = buildingComplaintRepo.save(buildingComp);
		buildingComp2 = buildingComplaintRepo.save(buildingComp2);
		facilityComp2 = facilitiesComplaintRepo.save(facilityComp2);

		// and we have 2 more complaints with status RESOLVING for the handler 1 

		int pageSize = 5;
		int page = 0;

		//get the complaints of for handler 1 (connected user)
		mockMvc.perform(get("/complaints-by-status?status=" + ComplaintStatus.RESOLVING + "&type=all" + "&user=me")
						.with(user(handler))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						// 5 complaints with status RESOLVING for handler 1
						.andExpect(jsonPath("$.results", Matchers.hasSize(pageSize)))
						//Check if all the complaints has the same handler (handler 1)
						.andExpect(jsonPath("$.results[*].handler.lastName", Matchers.everyItem(Matchers.is("Doe"))))
						// Check if all the complaints has the same status RESOLVING
						.andExpect(jsonPath("$.results[*].status", Matchers.everyItem(Matchers.is(ComplaintStatus.RESOLVING.toString()))))
						.andReturn();
		
		// get the complaints of for handler 2 (not connected user)
		pageSize = 1;
		page = 0;

		mockMvc.perform(get("/complaints-by-status?status=" + ComplaintStatus.RESOLVING + "&type=all" + "&user=" + handler2.getId())
						.with(user(handler))
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(pageSize))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						// 1 building complaints with status RESOLVING for handler 2
						.andExpect(jsonPath("$.results", Matchers.hasSize(pageSize)))
						//Check if all the complaints has the same user Ross
						.andExpect(jsonPath("$.results[*].handler.lastName", Matchers.everyItem(Matchers.is("handler"))))
						// Check if all the complaints has the same status RESOLVING
						.andExpect(jsonPath("$.results[*].status", Matchers.everyItem(Matchers.is(ComplaintStatus.RESOLVING.toString()))))
						// check the type of the complaints
						.andExpect(jsonPath("$.results[*].dtype", Matchers.everyItem(Matchers.is("BuildingComplaint"))))
						.andReturn();


		// delete the complaints and the handler 2
		roomComplaintRepo.delete(roomComp);
		buildingComplaintRepo.delete(buildingComp);
		buildingComplaintRepo.delete(buildingComp2);
		facilitiesComplaintRepo.delete(facilityComp2);
		userRepository.delete(handler2);

	}

	//Test update complaint specs---------------------------------------------
	@Test //update complaint status from resolving to resolved
	public void testUpdateComplaintStatus_shouldReturnChangedComplaint() throws Exception {
		// create a complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setHandler(handler.getUser());
		roomComp.setStatus(ComplaintStatus.RESOLVING);

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		// update dto
		EditComplaintStatusAndHandlerDto dto = new EditComplaintStatusAndHandlerDto();
		dto.setStatus(ComplaintStatus.RESOLVED);

		//update the complaint status with patch
		mockMvc.perform(patch("/complaints/" + roomComp.getId() + "/handling")
						.with(user(handler))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.status").value("RESOLVED"))
						.andReturn();

		//delete the complaint
		roomComplaintRepo.deleteById(roomComp.getId());
	}

	@Test // update status as a student (not handler - ADEI member)
	public void testUpdateComplaintStatus_shouldReturnForbidden() throws Exception {
		// create a complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setHandler(handler.getUser());
		roomComp.setStatus(ComplaintStatus.RESOLVING);

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		// update dto
		EditComplaintStatusAndHandlerDto dto = new EditComplaintStatusAndHandlerDto();
		dto.setStatus(ComplaintStatus.RESOLVED);

		//update the complaint status with patch
		mockMvc.perform(patch("/complaints/" + roomComp.getId() + "/handling")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isForbidden())
						.andReturn();

		//delete the complaint
		roomComplaintRepo.deleteById(roomComp.getId());
	}

	@Test
	public void testUpdateComplaintHandler_shouldReturnHandlerAndStatusChanged() throws Exception {
		// create a complaint
		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setDescription("The shower is broken");
		buildingComp.setComplainant(complainant.getUser());
		buildingComp.setDate(new Date());
		buildingComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		buildingComp = buildingComplaintRepo.save(buildingComp);

		// update dto
		EditComplaintStatusAndHandlerDto dto = new EditComplaintStatusAndHandlerDto();
		dto.setHandlerId(handler.getUser().getId());

		//update the complaint handler with patch (the handler is the user that is logged in)
		mockMvc.perform(patch("/complaints/" + buildingComp.getId() + "/handling")
						.with(user(handler))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.handler.id").value(handler.getUser().getId()))
						//the status should be changed to ASSIGNED because the handler is not null anymore
						.andExpect(jsonPath("$.status").value("ASSIGNED"))
						.andReturn();

		//delete the complaint
		buildingComplaintRepo.deleteById(buildingComp.getId());
	}

	@Test // update status from PENDING to ASSIGNED but the handler is null
	public void testUpdateComplaintStatus_invalidStatus() throws Exception {
		// create a complaint
		FacilitiesComplaint facilitiesComp = new FacilitiesComplaint();
		facilitiesComp.setFacilityType(FacilityType.PLAYGROUND);
		facilitiesComp.setComplainant(complainant.getUser());
		facilitiesComp.setDescription("The playground is broken");
		facilitiesComp.setDate(new Date());
		facilitiesComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		facilitiesComp = facilitiesComplaintRepo.save(facilitiesComp);

		// update dto
		EditComplaintStatusAndHandlerDto dto = new EditComplaintStatusAndHandlerDto();
		dto.setStatus(ComplaintStatus.ASSIGNED);

		//update the complaint status with patch
		mockMvc.perform(patch("/complaints/" + facilitiesComp.getId() + "/handling")
						.with(user(handler))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
						.andExpect(jsonPath("$.detail").value("Complaint must be assigned to a handler before changing the status"))
						.andReturn();

		//delete the complaint
		facilitiesComplaintRepo.deleteById(facilitiesComp.getId());

	}
	//End test update complaint specs-----------------------------------------

	//Test update complaint's details---------------------------------------------
	@Test //when status is PENDING (the complaint is not assigned to a handler yet)
	public void testUpdateComplaintDetails_shouldReturnUpdatedComplaint() throws Exception {
		// create a complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		// update dto
		CreateRoomComplaintDto dto = new CreateRoomComplaintDto();
		dto.setRoom("D16");
		dto.setDescription("The electricity is not working");
		dto.setRoomProb(RoomProb.ELECTRICITY);

		//update the complaint body with patch
		mockMvc.perform(patch("/complaints/" + roomComp.getId() + "/details")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.room").value("D16"))
						.andExpect(jsonPath("$.roomProb").value("ELECTRICITY"))
						.andReturn();

		//delete the complaint
		roomComplaintRepo.deleteById(roomComp.getId());
	}

	@Test //when status is ASSIGNED (the complaint is assigned to a handler)
	public void testUpdateComplaintDetails_shouldReturnForbidden() throws Exception {
		// create a complaint
		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setDescription("There is no electricity in the building");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setComplainant(complainant.getUser());
		buildingComp.setDate(new Date());
		buildingComp.setHandler(handler.getUser());
		buildingComp.setStatus(ComplaintStatus.ASSIGNED);

		//save the complaint
		buildingComp = buildingComplaintRepo.save(buildingComp);

		// update dto
		CreateBuildingComplaintDto dto = new CreateBuildingComplaintDto();
		dto.setBuilding("D");
		dto.setBuildingProb(BuildingProb.ELECTRICITY);
		dto.setDescription("There is no electricity in the building");

		//update the complaint body with patch
		mockMvc.perform(patch("/complaints/" + buildingComp.getId() + "/details")
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isForbidden())
						.andReturn();

		//delete the complaint
		buildingComplaintRepo.deleteById(buildingComp.getId());
	}

	@Test //update the complaint of another user
	public void testUpdateComplaintDetailsByOthers_shouldReturnForbidden() throws Exception {
		// create a complaint
		FacilitiesComplaint facilitiesComp = new FacilitiesComplaint();
		facilitiesComp.setFacilityType(FacilityType.PLAYGROUND);
		facilitiesComp.setComplainant(complainant2.getUser());
		facilitiesComp.setDescription("The playground is broken");
		facilitiesComp.setDate(new Date());
		facilitiesComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		facilitiesComp = facilitiesComplaintRepo.save(facilitiesComp);

		// update dto
		CreateFacilityComplaintDto dto = new CreateFacilityComplaintDto();
		dto.setFacilityType(FacilityType.GROCERY);
		dto.setDescription("Closed for a long time");

		//update the complaint body with patch
		mockMvc.perform(patch("/complaints/" + facilitiesComp.getId() + "/details")
						.with(user(complainant)) // complainant is not the owner of the complaint
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isForbidden())
						.andReturn();

		//delete the complaint
		facilitiesComplaintRepo.deleteById(facilitiesComp.getId());
	}
	//End test update complaint's details-----------------------------------------

	//Test delete complaint---------------------------------------------
	@Test
	public void testDeleteComplaint() throws Exception {
		// create a complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setDescription("The water is not working");
		roomComp.setDate(new Date());
		roomComp.setHandler(handler.getUser());
		roomComp.setStatus(ComplaintStatus.RESOLVING);

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		//delete the complaint
		mockMvc.perform(delete("/complaints/" + roomComp.getId())
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andReturn();

		//check if the complaint is deleted
		mockMvc.perform(get("/complaints/" + roomComp.getId())
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isNotFound())
						.andReturn();
	}

	@Test
	public void testDeleteComplaint_shouldReturnComplaintNotFound() throws Exception {
		// create a complaint
		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setStatus(ComplaintStatus.PENDING);
		buildingComp.setDescription("There is no electricity in the building");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setComplainant(complainant.getUser());
		buildingComp.setDate(new Date());

		//save the complaint
		buildingComp = buildingComplaintRepo.save(buildingComp);

		//test delete the complaint
		mockMvc.perform(delete("/complaints/" + buildingComp.getId() + 10) //the id is not correct
						.with(user(complainant))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isNotFound())
						.andReturn();
		
		//delete the complaint
		buildingComplaintRepo.deleteById(buildingComp.getId());
	}

	@Test //delete the complaint of another user
	public void testDeleteComplaintByOthers_shouldReturnForbidden() throws Exception {
		// create a complaint
		FacilitiesComplaint facilitiesComp = new FacilitiesComplaint();
		facilitiesComp.setFacilityType(FacilityType.PLAYGROUND);
		facilitiesComp.setComplainant(complainant2.getUser());
		facilitiesComp.setDescription("The playground is broken");
		facilitiesComp.setDate(new Date());
		facilitiesComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		facilitiesComp = facilitiesComplaintRepo.save(facilitiesComp);

		//delete the complaint
		mockMvc.perform(delete("/complaints/" + facilitiesComp.getId())
						.with(user(complainant)) // complainant is not the owner of the complaint
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isForbidden())
						.andReturn();

		//delete the complaint
		facilitiesComplaintRepo.deleteById(facilitiesComp.getId());
	}

	//End test delete complaint-----------------------------------------

	// Test if the dtype is sent correctly-----------------------------------------
	@Test
	public void testDTypeOfComplaint_shouldReturnCorrectDType() throws Exception {
		// create a complaint
		FacilitiesComplaint facilitiesComp = new FacilitiesComplaint();
		facilitiesComp.setFacilityType(FacilityType.PLAYGROUND);
		facilitiesComp.setComplainant(complainant2.getUser());
		facilitiesComp.setDescription("The playground is broken");
		facilitiesComp.setDate(new Date());
		facilitiesComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		facilitiesComp = facilitiesComplaintRepo.save(facilitiesComp);

		// get the complaint by id and check if the dtype is correct
		mockMvc.perform(get("/complaints/" + facilitiesComp.getId())
						.with(user(complainant2))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.dtype").value("FacilitiesComplaint"))
						.andReturn();

	}

	// Clean up database after all tests
	@AfterAll
	public void cleanUp() {
		roomComplaintRepo.deleteAll();
		facilitiesComplaintRepo.deleteAll();
		buildingComplaintRepo.deleteAll();
		userRepository.deleteAll();
	}

}
