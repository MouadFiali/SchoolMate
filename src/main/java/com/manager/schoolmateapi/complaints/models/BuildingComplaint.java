package com.manager.schoolmateapi.complaints.models;

import com.manager.schoolmateapi.complaints.enumerations.BuildingProb;

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
public class BuildingComplaint extends Complaint {
    
    @Column(name = "building", nullable = false)
    private String building;

    @Column(name = "building_prob", nullable = false)
    private BuildingProb buildingProb;

}
