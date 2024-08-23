package com.MapView.BackEnd.Controller;

import com.MapView.BackEnd.ServiceImp.LocationServiceImp;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.User.UserDetailsDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/ap1/v1/location")

public class LocationController {

    public final LocationServiceImp locationServiceImp;

    public LocationController(LocationServiceImp locationServiceImp) {
        this.locationServiceImp = locationServiceImp;
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:5173")
    @Transactional
    public ResponseEntity<LocationDetalsDTO> createLocation(LocationCreateDTO data, UriComponentsBuilder uriBuilder){
        //String email= jwt.getClaimAsString("email");
        var loc = locationServiceImp.createLocation(data);
        var uri  = uriBuilder.path("/ap1/v1/location/{location_id}").buildAndExpand(loc.id_location()).toUri();
        return ResponseEntity.created(uri).body(new LocationDetalsDTO(loc.id_location(),loc.post(),loc.enviroment()));
    }

    @GetMapping
    public ResponseEntity<List<LocationDetalsDTO>> getAllLocations (){
        var loc = locationServiceImp.getAllLocation();
        return ResponseEntity.ok(loc);
    }

    @GetMapping("/{location_id}")
    public ResponseEntity<LocationDetalsDTO> getLocation(@PathVariable("location_id") Long location_id){
        var loc = locationServiceImp.getLocation(location_id);
        return ResponseEntity.ok(loc);

    }
}
