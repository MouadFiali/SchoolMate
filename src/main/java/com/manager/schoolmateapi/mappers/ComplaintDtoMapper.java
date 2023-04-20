package com.manager.schoolmateapi.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusAndHandlerDto;
import com.manager.schoolmateapi.complaints.models.BuildingComplaint;
import com.manager.schoolmateapi.complaints.models.Complaint;
import com.manager.schoolmateapi.complaints.models.FacilitiesComplaint;
import com.manager.schoolmateapi.complaints.models.RoomComplaint;

@Mapper(componentModel = "spring")
public interface ComplaintDtoMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "handler", ignore = true)
    RoomComplaint createRoomComplaintDtoToRoomComplaint(CreateRoomComplaintDto createRoomComplaintDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "handler", ignore = true)
    BuildingComplaint createBuildingComplaintDtoToBuildingComplaint(CreateBuildingComplaintDto createBuildingComplaintDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "handler", ignore = true)
    FacilitiesComplaint createFacilityComplaintDtoToFacilityComplaint(CreateFacilityComplaintDto createFacilityComplaintDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    void updateComplaintStatusAndHandlerDtoToComplaint(EditComplaintStatusAndHandlerDto updateComplaintStatusDto, @MappingTarget Complaint complaint);

}
