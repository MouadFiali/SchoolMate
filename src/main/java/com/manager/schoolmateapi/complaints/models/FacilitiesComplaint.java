package com.manager.schoolmateapi.complaints.models;

import com.manager.schoolmateapi.complaints.enumerations.FacilityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper=false)
@Entity
public class FacilitiesComplaint extends Complaint {

    @Column(name = "facility_type", nullable = false)
    private FacilityType facilityType;

    @Column(name = "class", nullable = false)
    private String className;
    
}
