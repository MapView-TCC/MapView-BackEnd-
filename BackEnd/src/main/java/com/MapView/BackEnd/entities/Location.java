package com.MapView.BackEnd.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "location")
@Entity(name = "location")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id_location")

public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_location;
    private Long id_post;
    private Long id_enviroment;

}