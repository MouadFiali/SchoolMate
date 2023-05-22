package com.manager.schoolmateapi.complaints.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateBuildingComplaintDto.class, name = "building"),
        @JsonSubTypes.Type(value = CreateRoomComplaintDto.class, name = "room"),
        @JsonSubTypes.Type(value = CreateFacilityComplaintDto.class, name = "facility")
})
public class CreateComplaintDto {
    
    @NotBlank(message = "The description is required")
    protected String description;
    
}
