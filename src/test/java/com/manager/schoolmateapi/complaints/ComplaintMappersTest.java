package com.manager.schoolmateapi.complaints;

import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
import com.manager.schoolmateapi.mappers.ComplaintDtoMapper;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.enumerations.UserRole;
import com.manager.schoolmateapi.users.models.User;

@SpringBootTest
public class ComplaintMappersTest {
    @Autowired
    private ComplaintDtoMapper complaintMapper;

    @Autowired
    private UserRepository userRepository;

    //Note that the CreateRoomComplaintDto will also be user to edit a room complaint
    @Test
    public void testCreateRoomComplaintDtoToRoomComplaint() {
        CreateRoomComplaintDto createRoomComplaintDto = CreateRoomComplaintDto
                .builder()
                .description("The electricity is not working")
                .room("A20")
                .roomProb(RoomProb.ELECTRICITY)
                .build();
        
        RoomComplaint complaint = complaintMapper.createRoomComplaintDtoToRoomComplaint(createRoomComplaintDto);

        assertThat(complaint.getDescription(), Matchers.is(createRoomComplaintDto.getDescription()));
        assertThat(complaint.getRoom(), Matchers.is(createRoomComplaintDto.getRoom()));
        assertThat(complaint.getRoomProb(), Matchers.is(createRoomComplaintDto.getRoomProb()));
        
    }

    //Note that the CreateBuildingComplaintDto will also be user to edit a building complaint
    @Test
    public void testCreateBuildingComplaintDtoToBuildingComplaint() {
        CreateBuildingComplaintDto createBuildingComplaintDto = CreateBuildingComplaintDto
                .builder()
                .description("The water is cold in the shower")
                .building("B")
                .buildingProb(BuildingProb.SHOWER)
                .build();
        
        BuildingComplaint complaint = complaintMapper.createBuildingComplaintDtoToBuildingComplaint(createBuildingComplaintDto);

        assertThat(complaint.getDescription(), Matchers.is(createBuildingComplaintDto.getDescription()));
        assertThat(complaint.getBuilding(), Matchers.is(createBuildingComplaintDto.getBuilding()));
        assertThat(complaint.getBuildingProb(), Matchers.is(createBuildingComplaintDto.getBuildingProb()));

    }

    //Note that the CreateFacilityComplaintDto will also be user to edit a facility complaint
    @Test
    public void testCreateFacilityComplaintDtoToFacilityComplaint() {
        CreateFacilityComplaintDto createFacilityComplaintDto = CreateFacilityComplaintDto
                .builder()
                .description("The classroom A1 is closed very early")
                .facilityType(FacilityType.CLASS)
                .className("Amphi A1")
                .build();
        
        FacilitiesComplaint complaint = complaintMapper.createFacilityComplaintDtoToFacilityComplaint(createFacilityComplaintDto);

        assertThat(complaint.getDescription(), Matchers.is(createFacilityComplaintDto.getDescription()));
        assertThat(complaint.getFacilityType(), Matchers.is(createFacilityComplaintDto.getFacilityType()));
        assertThat(complaint.getClassName(), Matchers.is(createFacilityComplaintDto.getClassName()));

    }

    @Test
    public void testEditComplaintStatus(){
        EditComplaintStatusAndHandlerDto editComplaintStatusDto = EditComplaintStatusAndHandlerDto
                .builder()
                .status(ComplaintStatus.REJECTED)
                .build();

        RoomComplaint roomComplaint = RoomComplaint
                .builder()
                .description("The electricity is not working")
                .room("B41")
                .roomProb(RoomProb.ELECTRICITY)
                .build();

        complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintStatusDto, roomComplaint);
        
        assertThat(roomComplaint.getStatus(), Matchers.is(editComplaintStatusDto.getStatus()));
        //To check if the room and the complaint is not changed
        assertThat(roomComplaint.getRoom(), Matchers.is("B41"));
    }

    @Test
    public void testEditComplaintHandler(){
        //Create a user to be the handler
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Smith");
		user.setRole(UserRole.STUDENT);
		user.setPassword("password");
		user.setEmail("john.smith@gmail.com");

		// Save the user
		userRepository.save(user);

        //get the id to delete it later
        Long id = user.getId();

        EditComplaintStatusAndHandlerDto editComplaintHandlerDto = EditComplaintStatusAndHandlerDto
                .builder()
                .handlerId(id)
                .build();

        RoomComplaint roomComplaint = RoomComplaint
                .builder()
                .description("The electricity is not working")
                .room("B41")
                .roomProb(RoomProb.ELECTRICITY)
                .build();

        complaintMapper.updateComplaintStatusAndHandlerDtoToComplaint(editComplaintHandlerDto, roomComplaint);
        
        assertThat(roomComplaint.getHandler(), Matchers.is(userRepository.findById(editComplaintHandlerDto.getHandlerId()).get()));
        //To check if the room and the complaint is not changed
        assertThat(roomComplaint.getRoom(), Matchers.is("B41"));

        //Delete the user
        userRepository.deleteById(id);
    }


    
}
