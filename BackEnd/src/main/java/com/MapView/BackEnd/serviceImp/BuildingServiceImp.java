package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.BuildingRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.BuildingService;
import com.MapView.BackEnd.dtos.Building.BuildingCreateDTO;
import com.MapView.BackEnd.dtos.Building.BuildingDetailsDTO;
import com.MapView.BackEnd.dtos.Building.BuildingUpdateDTO;
import com.MapView.BackEnd.entities.Building;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class BuildingServiceImp implements BuildingService {


    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private  final UserLogRepository userLogRepository;

    public BuildingServiceImp(BuildingRepository buildingRepository, UserRepository userRepository, UserLogRepository userLogRepository) {
        this.buildingRepository = buildingRepository;
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
    }

    @Override
    public BuildingDetailsDTO getBuilding(Long building_id,Long user_id) {
        Building building = this.buildingRepository.findById(building_id).orElseThrow(() -> new NotFoundException("Id not found"));
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        if (!building.isOperative()){
            throw new OperativeFalseException("The inactive Build cannot be read..");
        }
        var userLog = new UserLog(user,"Building",building_id.toString(),"Read building",EnumAction.READ);
        userLogRepository.save(userLog);

        return new BuildingDetailsDTO(building);
    }

    @Override
    public List<BuildingDetailsDTO> getAllBuilding(int page, int itens, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        return buildingRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(BuildingDetailsDTO::new).toList();
    }

    @Override
    public BuildingDetailsDTO createBuilding(BuildingCreateDTO dados,Long user_id) {
        var building = new Building(dados);
        Long id_build = buildingRepository.save(building).getId_building();
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var userLog = new UserLog(user,"Building",id_build.toString(),"Create new Building", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new BuildingDetailsDTO(building);
    }

    @Override
    public BuildingDetailsDTO updateBuilding(Long building_id, BuildingUpdateDTO dados,Long user_id) {
        var building = buildingRepository.findById(building_id). orElseThrow(() -> new RuntimeException("Building Id not found"));
        if(!building.isOperative()){
            throw new OperativeFalseException("The inactive equipment cannot be updated.");
        }

        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Building",building_id.toString(),null,"Update building: ",EnumAction.UPDATE);

        if (dados.building_code() != null){
            building.setBuilding_code(dados.building_code());

            userlog.setField("building_code");
            userlog.setDescription("building_code to:"+ dados.building_code());
        }

        buildingRepository.save(building);
        userLogRepository.save(userlog);
        return new BuildingDetailsDTO(building);
    }

    @Override
    public void activateBuilding(Long building_id, Long user_id) {
        var buildingClass = buildingRepository.findById(building_id);
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        if (buildingClass.isPresent()){
            var building = buildingClass.get();
            building.setOperative(true);
        }
        var userLog = new UserLog(null,"Building",building_id.toString(),"Operative","Activated Area", EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateBuilding(Long building_id,Long user_id) {
        var buildingClass = buildingRepository.findById(building_id);
        if (buildingClass.isPresent()){
            var building = buildingClass.get();
            building.setOperative(false);
        }
        var userLog = new UserLog(null,"Building",building_id.toString(),"Operative","Inactivated Area", EnumAction.UPDATE);
         userLogRepository.save(userLog);
    }
}
