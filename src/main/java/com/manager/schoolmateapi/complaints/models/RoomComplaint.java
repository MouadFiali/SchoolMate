package com.manager.schoolmateapi.complaints.models;

import com.manager.schoolmateapi.complaints.enumerations.RoomProb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class RoomComplaint extends Complaint {
    
    @Column(name = "room", nullable = false)
    private String room;

    @Column(name = "room_problem", nullable = false)
    private RoomProb roomProb;

}
