package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.infra.LocationAlreadyExistsException;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.EnviromentRepository;
import com.MapView.BackEnd.repository.LocationRepository;
import com.MapView.BackEnd.repository.PostRepository;
import com.MapView.BackEnd.service.LocationService;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.Location.LocationUpdateDTO;
import com.MapView.BackEnd.entities.Location;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
    public List<LocationDetalsDTO> getAllLocation( int page, int itens) {
        return this.locationRepository.findAll(PageRequest.of(page,itens)).stream().map(LocationDetalsDTO::new).toList();
    }

    @Override
    public LocationDetalsDTO createLocation(LocationCreateDTO data) {
        try {
            var post = postRepository.findById(data.id_post()).orElseThrow(() -> new NotFoundException("Post Id not Found"));
            if (!post.isOperative()) {
                throw new OperativeFalseException("The inactive post cannot be accessed.");
            }

            var enviroment = enviromentRepository.findById(data.id_eviroment()).orElseThrow(() -> new NotFoundException("Enviroment Id Not Found"));
            if (!enviroment.isOperative()) {
                throw new OperativeFalseException("The inactive enviroment cannot be accessed.");
            }

            var location = new Location(post, enviroment);
            locationRepository.save(location);
            return new LocationDetalsDTO(location);

        }catch (DataIntegrityViolationException e ){
            throw new LocationAlreadyExistsException("The location with these values has been created");
        }

    }

    @Override
    public LocationDetalsDTO updateLocation(Long id_location, LocationUpdateDTO data) {
        var location = locationRepository.findById(id_location).orElseThrow(() -> new NotFoundException("Location Id Not Found"));

        var post = postRepository.findById(data.id_post()).orElseThrow(() -> new NotFoundException("Post Id not Found"));
        if(!post.isOperative()){
            throw new OperativeFalseException("The inactive post cannot be accessed.");
        }
        var enviroment = enviromentRepository.findById(data.id_enviroment()).orElseThrow(() -> new NotFoundException("Enviroment Id Not Found"));
        if(!enviroment.isOperative()){
            throw new OperativeFalseException("The inactive enviroment cannot be accessed.");
        }

        if(data.id_enviroment() != null){
            location.setEnvironment(enviroment);
        }
        if(data.id_post() != null){
            location.setPost(post);
        }
        locationRepository.save(location);
        return new LocationDetalsDTO(location);
    }
}
