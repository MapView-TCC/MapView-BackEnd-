package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.cfg.Environment;

import java.util.List;

@Table(name = "location", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_post","id_environment"})})
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

    @ManyToOne
    @JoinColumn(name = "id_post")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "id_environment")
    private Enviroment environment;

    public Location(Post post, Enviroment enviroment){
        this.post = post;
        this.environment = enviroment;
    }
}