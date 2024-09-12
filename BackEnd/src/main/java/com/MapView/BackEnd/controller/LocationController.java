package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.serviceImp.LocationServiceImp;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
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
        var uri  = uriBuilder.path("/ap1/v1/location/{id_location}").buildAndExpand(loc.id_location()).toUri();
        return ResponseEntity.created(uri).body(new LocationDetalsDTO(loc.id_location(),loc.post(),loc.enviroment()));
    }

    @GetMapping
    public ResponseEntity<List<LocationDetalsDTO>> getAllLocations (@RequestParam int page, @RequestParam int itens){
        var loc = locationServiceImp.getAllLocation(page, itens);
        return ResponseEntity.ok(loc);
    }

    @GetMapping("/{id_location}")
    public ResponseEntity<LocationDetalsDTO> getLocation(@PathVariable("id_location") Long id_location){
        var loc = locationServiceImp.getLocation(id_location);
        return ResponseEntity.ok(loc);

    }
}
