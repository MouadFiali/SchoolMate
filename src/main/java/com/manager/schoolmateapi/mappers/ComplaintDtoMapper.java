package com.manager.schoolmateapi.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusAndHandlerDto;
import com.manager.schoolmateapi.complaints.models.BuildingComplaint;
import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;
import com.manager.schoolmateapi.complaints.models.RoomComplaint;
import com.manager.schoolmateapi.users.UserRepository;
import com.manager.schoolmateapi.users.models.User;

@Mapper(componentModel = "spring")
public abstract class ComplaintDtoMapper {

    @Autowired
    private UserRepository userRepository;
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "handler", ignore = true)
    @Mapping(target = "dtype", ignore = true)
    public abstract RoomComplaint createRoomComplaintDtoToRoomComplaint(CreateRoomComplaintDto createRoomComplaintDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "handler", ignore = true)
    @Mapping(target = "dtype", ignore = true)
    public abstract BuildingComplaint createBuildingComplaintDtoToBuildingComplaint(CreateBuildingComplaintDto createBuildingComplaintDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "handler", ignore = true)
    @Mapping(target = "dtype", ignore = true)
    public abstract FacilitiesComplaint createFacilityComplaintDtoToFacilityComplaint(CreateFacilityComplaintDto createFacilityComplaintDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(source = "handlerId", target = "handler", qualifiedByName = "handlerIdToHandlerUser")
    @Mapping(target = "dtype", ignore = true)
    public abstract Complaint updateComplaintStatusAndHandlerDtoToComplaint(EditComplaintStatusAndHandlerDto updateComplaintStatusDto, @MappingTarget Complaint complaint);

    @Named("handlerIdToHandlerUser")
    public User handlerIdToHandlerUser(Long handlerId) {
        return userRepository.findById(handlerId).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Handler with id: #%d not found", handlerId));
          });
    }
}
