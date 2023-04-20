package com.manager.schoolmateapi.complaints.dto;

import com.manager.schoolmateapi.complaints.enumerations.ComplaintStatus;
import com.manager.schoolmateapi.users.models.User;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditComplaintStatusAndHandlerDto {
    
    @Nullable
    private ComplaintStatus status;

    @Nullable
    private User handler;

}
