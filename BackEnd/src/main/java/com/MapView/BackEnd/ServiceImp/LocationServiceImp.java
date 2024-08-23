package com.MapView.BackEnd.ServiceImp;

import com.MapView.BackEnd.Repository.EnviromentRepository;
import com.MapView.BackEnd.Repository.LocationRepository;
import com.MapView.BackEnd.Repository.PostRepository;
import com.MapView.BackEnd.Service.LocationService;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.Location.LocationUpdateDTO;
import com.MapView.BackEnd.entities.Enviroment;
import com.MapView.BackEnd.entities.Location;
import com.MapView.BackEnd.entities.Post;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImp implements LocationService {

    private final LocationRepository locationRepository;
    private final PostRepository postRepository;
    private final EnviromentRepository enviromentRepository;

    public LocationServiceImp(LocationRepository locationRepository, PostRepository postRepository, EnviromentRepository enviromentRepository) {
        this.locationRepository = locationRepository;
        this.postRepository = postRepository;
        this.enviromentRepository = enviromentRepository;
    }

    @Override
    public LocationDetalsDTO getLocation(Long id_location) {
        var loc = locationRepository.findById(id_location).orElseThrow(() -> new NotFoundException("Location id not found"));
        return new LocationDetalsDTO(loc);
    }

    @Override
    public List<LocationDetalsDTO> getAllLocation() {
        return this.locationRepository.findAll().stream().map(LocationDetalsDTO::new).toList();
    }

    @Override
    public LocationDetalsDTO createLocation(LocationCreateDTO data) {
        var post = postRepository.findById(data.id_post()).orElseThrow(() -> new NotFoundException("Post Id not Found"));
        var enviroment = enviromentRepository.findById(data.id_eviroment()).orElseThrow(() -> new NotFoundException("Enviroment Id Not Found"));

        var location = new Location(post,enviroment);
        locationRepository.save(location);
        return new LocationDetalsDTO(location);
    }

    @Override
    public LocationDetalsDTO updateLocation(Long id_location, LocationUpdateDTO data) {
        var post = postRepository.findById(data.id_post()).orElseThrow(() -> new NotFoundException("Post Id not Found"));
        var enviroment = enviromentRepository.findById(data.id_enviromnet()).orElseThrow(() -> new NotFoundException("Enviroment Id Not Found"));
        var location = locationRepository.findById(id_location).orElseThrow(() -> new NotFoundException("Location Id Not Found"));

        if(data.id_enviromnet() != null){
            location.setEnviroment(enviroment);
        }
        if(data.id_post() != null){
            location.setPost(post);
        }
        locationRepository.save(location);

        return new LocationDetalsDTO(location);
    }
}
