package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.complaints.enumerations.RoomProb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateRoomComplaintDto extends CreateComplaintDto {
    
    @NotBlank(message = "The room name is required")
    @Pattern(regexp = "[a-zA-Z][1-9]{1,2}$",
    message = "The room name is invalid, please give the name of the room in the format 'A1' or 'B12'")
    private String room;

    @NotBlank(message = "The type of the problem is required")
    private RoomProb roomProb;
    
}
