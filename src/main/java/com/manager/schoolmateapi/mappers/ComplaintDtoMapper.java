package com.manager.schoolmateapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.manager.schoolmateapi.complaints.dto.CreateBuildingComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateFacilityComplaintDto;
import com.manager.schoolmateapi.complaints.dto.CreateRoomComplaintDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintHandlerDto;
import com.manager.schoolmateapi.complaints.dto.EditComplaintStatusDto;
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
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "handler", ignore = true)
    void updateComplaintStatusDtoToComplaint(EditComplaintStatusDto updateComplaintStatusDto, @MappingTarget Complaint complaint);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "complainant", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateComplaintHandlerDtoToComplaint(EditComplaintHandlerDto updateComplaintHandlerDto, @MappingTarget Complaint complaint);
}
