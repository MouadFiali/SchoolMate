package com.manager.schoolmateapi.complaints;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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
		// Clear the database
		buildingComplaintRepo.deleteAll();
		facilitiesComplaintRepo.deleteAll();
		roomComplaintRepo.deleteAll();
		userRepository.deleteAll();

	
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
		buildingComp.setDate(LocalDate.now());

		// Room complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("C36");
		roomComp.setRoomProb(RoomProb.OTHER);
		roomComp.setDescription("The room does not have a window");
		roomComp.setComplainant(complainant.getUser());
		roomComp.setHandler(handler.getUser());
		roomComp.setStatus(ComplaintStatus.ASSIGNED);
		roomComp.setDate(LocalDate.now());

		// Facility complaint
		FacilitiesComplaint facilityComp = new FacilitiesComplaint();
		facilityComp.setFacilityType(FacilityType.PLAYGROUND);
		facilityComp.setDescription("The basketball court does not have a net");
		facilityComp.setComplainant(complainant.getUser());
		facilityComp.setHandler(handler.getUser());
		facilityComp.setStatus(ComplaintStatus.RESOLVING);
		facilityComp.setDate(LocalDate.now());

		//Facility complaint 2 (claimed by complainant 2)
		FacilitiesComplaint facilityComp2 = new FacilitiesComplaint();
		facilityComp2.setFacilityType(FacilityType.CLASS);
		facilityComp2.setClassName("Amphi 3");
		facilityComp2.setDescription("The classroom does not have a projector");
		facilityComp2.setComplainant(complainant2.getUser());
		facilityComp2.setHandler(handler.getUser());
		facilityComp2.setStatus(ComplaintStatus.CONFIRMED);
		facilityComp2.setDate(LocalDate.now());

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
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isCreated())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.building").value("B"))
						.andExpect(jsonPath("$.buildingProb").value("SHOWER"))
						.andExpect(jsonPath("$.handler").value(IsNull.nullValue()))
						.andExpect(jsonPath("$.complainant.lastName").value("Smith"))
						.andExpect(jsonPath("$.status").value("PENDING"))
						.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
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
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType("application/json"))
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
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.errors").value("The class name should not be empty if the facility type is a class"))
						.andReturn();
	}

	@Test //Test create building complaint with wrong building prob
	public void testCreateBuildingComplaint_wrongBuildingProb_shouldReturnBadRequest() throws Exception {
		String dto = "{\"building\":\"B\",\"buildingProb\":\"SHOWER\",\"description\":\"The building does not have electricity\"}";

		mockMvc.perform(post("/complaints")
						.with(user(complainant))
						.contentType("application/json")
						.content(dto))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.errors").value("The building problem should be one of the following: ELECTRICITY, WATER, SHOWER, OTHER"))
						.andReturn();
	}
	//End test create complaints by type -------------------------------

	//Test get complaint by type----------------------------------------
	@Test //Test get building complaints of all users
	public void testGetBuildingComplaints_shouldReturnAllBuildingComps() throws Exception {
		mockMvc.perform(get("/complaints?type=building&scope=all")
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$[0].building").value("A"))
						.andExpect(jsonPath("$[0].buildingProb").value("ELECTRICITY"))
						.andExpect(jsonPath("$[0].handler").value(IsNull.nullValue()))
						.andExpect(jsonPath("$[0].status").value("PENDING"))
						.andReturn();
	}

	@Test //Test get facilities complaints of a specific user
	public void testGetFacilitiesComplaints_shouldReturnFacilitiesCompsForUser() throws Exception {
		mockMvc.perform(get("/complaints?type=facilities&scope=user")
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.size()").value(1))
						.andExpect(jsonPath("$[0].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$[0].handler.lastName").value("Doe"))
						.andExpect(jsonPath("$[0].complainant.lastName").value("Smith"))
						.andExpect(jsonPath("$[0].status").value("RESOLVING"))
						.andReturn();
	}

	@Test //Test get facilities complaints of all users
	public void testGetFacilitiesComplaints_shouldReturnFacilitiesCompsForAll() throws Exception {
		mockMvc.perform(get("/complaints?type=facilities&scope=all")
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.size()").value(2))
						//First complaint
						.andExpect(jsonPath("$[0].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$[0].handler.lastName").value("Doe"))
						.andExpect(jsonPath("$[0].complainant.lastName").value("Smith"))
						.andExpect(jsonPath("$[0].status").value("RESOLVING"))
						//Second complaint
						.andExpect(jsonPath("$[1].facilityType").value("CLASS"))
						.andExpect(jsonPath("$[1].handler.lastName").value("Doe"))
						.andExpect(jsonPath("$[1].complainant.lastName").value("Ross"))
						.andExpect(jsonPath("$[1].status").value("CONFIRMED"))
						.andReturn();
	}

	@Test //Test get all complaints of a specific user
	public void testGetAllComplaints_shouldReturnAllComplaintsForUser() throws Exception{
		mockMvc.perform(get("/complaints?scope=user") // or /complaints?type=all&scope=user
						.with(user(complainant)) //John Smith
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.size()").value(3))
						//First complaint
						.andExpect(jsonPath("$[0].building").value("A"))
						.andExpect(jsonPath("$[0].complainant.lastName").value("Smith"))
						//Second complaint
						.andExpect(jsonPath("$[1].room").value("C36"))
						.andExpect(jsonPath("$[1].complainant.lastName").value("Smith"))
						//Third complaint
						.andExpect(jsonPath("$[2].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$[2].complainant.lastName").value("Smith"))
						.andReturn();
	}

	
	@Test //Test get all complaints of all users
	public void testGetAllComplaints_shouldReturnAllComplaintsForAll() throws Exception{
		mockMvc.perform(get("/complaints?scope=all") // or /complaints?type=all&scope=all
						.with(user(complainant)) //John Smith
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.size()").value(4))
						//First complaint
						.andExpect(jsonPath("$[0].building").value("A"))
						.andExpect(jsonPath("$[0].complainant.lastName").value("Smith"))
						//Second complaint
						.andExpect(jsonPath("$[1].room").value("C36"))
						.andExpect(jsonPath("$[1].complainant.lastName").value("Smith"))
						//Third complaint
						.andExpect(jsonPath("$[2].facilityType").value("PLAYGROUND"))
						.andExpect(jsonPath("$[2].complainant.lastName").value("Smith"))
						//Fourth complaint
						.andExpect(jsonPath("$[3].facilityType").value("CLASS"))
						.andExpect(jsonPath("$[3].complainant.lastName").value("Ross"))
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
		roomComp.setDate(LocalDate.now());
		roomComp.setDescription("The water is not working");

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		//get the complaint by id
		mockMvc.perform(get("/complaints/" + roomComp.getId())
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
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
						.contentType("application/json"))
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
		roomComp.setStatus(ComplaintStatus.PENDING);

		RoomComplaint roomComp2 = new RoomComplaint();
		roomComp2.setRoom("C8");
		roomComp2.setRoomProb(RoomProb.ELECTRICITY);
		roomComp2.setComplainant(complainant2.getUser());
		roomComp2.setStatus(ComplaintStatus.PENDING);

		//save the complaints
		roomComp = roomComplaintRepo.save(roomComp);
		roomComp2 = roomComplaintRepo.save(roomComp2);

		//get the complaints of complainant 2
		mockMvc.perform(get("/complaints?type=room&user=" + complainant2.getUser().getId())
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.size()").value(2))
						//Check if all the complaints has the same user Ross
						.andExpect(jsonPath("$[*].complainant.lastName", Matchers.everyItem(Matchers.is("Ross"))))
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
		roomComp.setStatus(ComplaintStatus.PENDING);

		RoomComplaint roomComp2 = new RoomComplaint();
		roomComp2.setRoom("C8");
		roomComp2.setRoomProb(RoomProb.ELECTRICITY);
		roomComp2.setComplainant(complainant2.getUser());
		roomComp2.setStatus(ComplaintStatus.PENDING);

		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setComplainant(complainant2.getUser());
		buildingComp.setStatus(ComplaintStatus.PENDING);

		//save the complaints
		roomComp = roomComplaintRepo.save(roomComp);
		roomComp2 = roomComplaintRepo.save(roomComp2);
		buildingComp = buildingComplaintRepo.save(buildingComp);

		//get the complaints of complainant 2
		mockMvc.perform(get("/complaints?user=" + complainant2.getUser().getId())
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						// 2 room complaints, 1 building complaint and 1 facility complaint = 4
						.andExpect(jsonPath("$.size()").value(4))
						//Check if all the complaints has the same user Ross
						.andExpect(jsonPath("$[*].complainant.lastName", Matchers.everyItem(Matchers.is("Ross"))))
						.andReturn();

		//delete the complaints
		roomComplaintRepo.deleteById(roomComp.getId());
		roomComplaintRepo.deleteById(roomComp2.getId());
		buildingComplaintRepo.deleteById(buildingComp.getId());

	}
	//End test get complaint of a specific user--------------------------

	//Test update complaint specs---------------------------------------------
	@Test //update complaint status from resolving to resolved
	public void testUpdateComplaintStatus_shouldReturnChangedComplaint() throws Exception {
		// create a complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
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
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
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
						.contentType("application/json")
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
		buildingComp.setDate(LocalDate.now());
		buildingComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		buildingComp = buildingComplaintRepo.save(buildingComp);

		// update dto
		EditComplaintStatusAndHandlerDto dto = new EditComplaintStatusAndHandlerDto();
		dto.setHandlerId(handler.getUser().getId());

		//update the complaint handler with patch (the handler is the user that is logged in)
		mockMvc.perform(patch("/complaints/" + buildingComp.getId() + "/handling")
						.with(user(handler))
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
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
		facilitiesComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		facilitiesComp = facilitiesComplaintRepo.save(facilitiesComp);

		// update dto
		EditComplaintStatusAndHandlerDto dto = new EditComplaintStatusAndHandlerDto();
		dto.setStatus(ComplaintStatus.ASSIGNED);

		//update the complaint status with patch
		mockMvc.perform(patch("/complaints/" + facilitiesComp.getId() + "/handling")
						.with(user(handler))
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isBadRequest())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.message").value("The handler is null, the status must be PENDING"))
						.andReturn();

		//delete the complaint
		facilitiesComplaintRepo.deleteById(facilitiesComp.getId());

	}
	//End test update complaint specs-----------------------------------------

	//Test update complaint's details---------------------------------------------
	@Test //when status is PENDING (the complaint is not assigned to a handler yet)
	public void testUpdateComplaintBody_shouldReturnUpdatedComplaint() throws Exception {
		// create a complaint
		RoomComplaint roomComp = new RoomComplaint();
		roomComp.setRoom("D15");
		roomComp.setRoomProb(RoomProb.WATER);
		roomComp.setComplainant(complainant.getUser());
		roomComp.setStatus(ComplaintStatus.PENDING);

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		// update dto
		CreateRoomComplaintDto dto = new CreateRoomComplaintDto();
		dto.setRoom("D16");
		dto.setRoomProb(RoomProb.ELECTRICITY);

		//update the complaint body with patch
		mockMvc.perform(patch("/complaints/" + roomComp.getId() + "/details")
						.with(user(complainant))
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json"))
						.andExpect(jsonPath("$.room").value("D16"))
						.andExpect(jsonPath("$.roomProb").value("ELECTRICITY"))
						.andReturn();

		//delete the complaint
		roomComplaintRepo.deleteById(roomComp.getId());
	}

	@Test //when status is ASSIGNED (the complaint is assigned to a handler)
	public void testUpdateComplaintBody_shouldReturnForbidden() throws Exception {
		// create a complaint
		BuildingComplaint buildingComp = new BuildingComplaint();
		buildingComp.setBuilding("E");
		buildingComp.setDescription("There is no electricity in the building");
		buildingComp.setBuildingProb(BuildingProb.SHOWER);
		buildingComp.setComplainant(complainant.getUser());
		buildingComp.setHandler(handler.getUser());
		buildingComp.setStatus(ComplaintStatus.ASSIGNED);

		//save the complaint
		buildingComp = buildingComplaintRepo.save(buildingComp);

		// update dto
		CreateBuildingComplaintDto dto = new CreateBuildingComplaintDto();
		dto.setBuilding("D");
		dto.setBuildingProb(BuildingProb.ELECTRICITY);

		//update the complaint body with patch
		mockMvc.perform(patch("/complaints/" + buildingComp.getId() + "/details")
						.with(user(complainant))
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(dto)))
						.andExpect(status().isForbidden())
						.andReturn();

		//delete the complaint
		buildingComplaintRepo.deleteById(buildingComp.getId());
	}

	@Test //update the complaint of another user
	public void testUpdateComplaintBodyByOthers_shouldReturnForbidden() throws Exception {
		// create a complaint
		FacilitiesComplaint facilitiesComp = new FacilitiesComplaint();
		facilitiesComp.setFacilityType(FacilityType.PLAYGROUND);
		facilitiesComp.setComplainant(complainant2.getUser());
		facilitiesComp.setDescription("The playground is broken");
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
						.contentType("application/json")
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
		roomComp.setHandler(handler.getUser());
		roomComp.setStatus(ComplaintStatus.RESOLVING);

		//save the complaint
		roomComp = roomComplaintRepo.save(roomComp);

		//delete the complaint
		mockMvc.perform(delete("/complaints/" + roomComp.getId())
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isOk())
						.andReturn();

		//check if the complaint is deleted
		mockMvc.perform(get("/complaints/" + roomComp.getId())
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isNotFound())
						.andReturn();
	}

	@Test
	public void testDeleteComplaint_shouldReturnComplaintNotFound() throws Exception {
		//delete the complaint
		mockMvc.perform(delete("/complaints/" + 9999999)
						.with(user(complainant))
						.contentType("application/json"))
						.andExpect(status().isNotFound())
						.andReturn();
	}
	//End test delete complaint-----------------------------------------


}
