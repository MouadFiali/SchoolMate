package com.manager.schoolmateapi.placesuggestions;

import org.springframework.data.geo.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.manager.schoolmateapi.placesuggestions.enumerations.PlaceSuggestionType;
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
@Table(name = "placesuggestions")
public class PlaceSuggestions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PlaceSuggestionType suggestiontype;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Point coordinates;

}
