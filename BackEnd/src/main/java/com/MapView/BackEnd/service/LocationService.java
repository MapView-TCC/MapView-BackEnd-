package com.MapView.BackEnd.service;

import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.Location.LocationUpdateDTO;

import java.util.List;

public interface LocationService {

    LocationDetalsDTO getLocation(Long id_location);
    List<LocationDetalsDTO> getAllLocation();
    LocationDetalsDTO createLocation(LocationCreateDTO data);
    LocationDetalsDTO updateLocation(Long id_location, LocationUpdateDTO data);


}