package com.manager.schoolmateapi.complaints.models;

import com.manager.schoolmateapi.complaints.enumerations.FacilityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class FacilitiesComplaint extends Complaint {

    @Column(name = "facility_type", nullable = false)
    private FacilityType facilityType;

    @Column(name = "class", nullable = false)
    private String className;
}
