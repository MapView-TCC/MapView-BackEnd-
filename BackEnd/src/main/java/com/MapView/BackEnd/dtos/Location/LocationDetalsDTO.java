package com.MapView.BackEnd.dtos.Location;

import com.MapView.BackEnd.entities.Enviroment;
import com.MapView.BackEnd.entities.Location;
import com.MapView.BackEnd.entities.Post;

public record LocationDetalsDTO (Long id_location, Post post, Enviroment enviroment) {
    public LocationDetalsDTO(Location location){
        this(location.getId_location(), location.getPost(),location.getEnviroment());
    }
}
