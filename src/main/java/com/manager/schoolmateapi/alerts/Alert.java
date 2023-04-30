package com.manager.schoolmateapi.alerts;

import org.springframework.data.geo.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.manager.schoolmateapi.alerts.enumerations.AlertStatus;
import com.manager.schoolmateapi.alerts.enumerations.AlertType;
import com.manager.schoolmateapi.users.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "alerts")
public class Alert {
    private static final AlertStatus PENDING = null;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private AlertType type;

    @Column(nullable = false)
    private Point coordinates;

    @Column(nullable = false)
    private AlertStatus status = PENDING ;
}
