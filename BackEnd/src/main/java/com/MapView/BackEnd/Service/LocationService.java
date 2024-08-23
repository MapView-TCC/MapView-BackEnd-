package com.MapView.BackEnd.Service;

import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.Location.LocationUpdateDTO;
import com.MapView.BackEnd.entities.Enviroment;
import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.Post;
import com.MapView.BackEnd.entities.Responsible;

import java.time.LocalDate;
import java.util.List;

public interface LocationService {

    LocationDetalsDTO getLocation(Long id_location);
    List<LocationDetalsDTO> getAllLocation();
    LocationDetalsDTO createLocation(LocationCreateDTO data);
    LocationDetalsDTO updateLocation(Long id_location, LocationUpdateDTO data);


}
