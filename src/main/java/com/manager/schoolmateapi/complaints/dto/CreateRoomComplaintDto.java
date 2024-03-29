package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.complaints.enumerations.RoomProb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateRoomComplaintDto extends CreateComplaintDto {
    
    @NotBlank(message = "The room name is required")
    @Pattern(regexp = "[a-zA-Z]\\s*[0-9]+",
    message = "The room name is invalid, please give the name of the room in the format 'A1' or 'B12'")
    private String room;

    @NotNull(message = "The room problem is required")
    private RoomProb roomProb;

}
