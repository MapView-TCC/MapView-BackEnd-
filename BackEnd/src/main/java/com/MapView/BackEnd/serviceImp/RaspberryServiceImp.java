package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.*;
import com.MapView.BackEnd.service.RaspberryService;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryCreateDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryDetailsDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryUpdateDTO;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaspberryServiceImp implements RaspberryService {

    private final RaspberryRepository raspberryRepository;
    private final AreaRepository areaRepository;
    private final BuildingRepository buildingRepository;
    private final UserLogRepository userLogRepository;

    private final UserRepository userRepository;

    public RaspberryServiceImp(RaspberryRepository raspberryRepository, AreaRepository areaRepository, BuildingRepository buildingRepository, UserLogRepository userLogRepository, UserRepository userRepository) {
        this.raspberryRepository = raspberryRepository;
        this.areaRepository = areaRepository;
        this.buildingRepository = buildingRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
    }


    @Override
    public RaspberryDetailsDTO getRaspberry(String id_Raspberry, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        Raspberry raspberry = this.raspberryRepository.findById(id_Raspberry)
                .orElseThrow(() -> new NotFoundException("Raspberry id not found"));
        if (!raspberry.isOperative()){
            throw new OperativeFalseException("The inactive raspberry cannot be accessed.");
        }

        var userLog = new UserLog(user,"Raspberry",id_Raspberry.toString(),"Read Raspberry",EnumAction.READ);
        userLogRepository.save(userLog);

        return new RaspberryDetailsDTO(raspberry);
    }

    @Override
    public List<RaspberryDetailsDTO> getAllRaspberry(int page, int itens, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Raspberry","Read All Raspberry", EnumAction.READ);
        userLogRepository.save(userLog);

        return raspberryRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(RaspberryDetailsDTO::new).toList();
    }

    @Override
    public RaspberryDetailsDTO createRaspberry(RaspberryCreateDTO raspberryCreateDTO, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        Building building = buildingRepository.findById(raspberryCreateDTO.id_building())
                .orElseThrow(() -> new NotFoundException("Building id not found."));
        if (!building.isOperative()){
            throw new OperativeFalseException("The inactive building cannot be accessed.");
        }

        Area area = areaRepository.findById(raspberryCreateDTO.id_area())
                .orElseThrow(() -> new NotFoundException("Area id not found"));
        if (!area.isOperative()){
            throw new OperativeFalseException("The inactive area cannot be accessed.");
        }

        Raspberry raspberry = new Raspberry(raspberryCreateDTO, building, area);
        String id_raspberry = raspberryRepository.save(raspberry).getId_raspberry();

        var userLog = new UserLog(user,"Raspberry", id_raspberry,"Create new Raspberry", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new RaspberryDetailsDTO(raspberry);

    }

    @Override
    public RaspberryDetailsDTO updateRaspberry(String id_raspberry, RaspberryUpdateDTO dados, Long user_id) {
        var raspberry = raspberryRepository.findById(id_raspberry)
                .orElseThrow(() -> new NotFoundException("Raspberry id not found"));
        if(!raspberry.isOperative()){
            throw new OperativeFalseException("The inactive raspberry:"+ raspberry.getId_raspberry() + "cannot be accessed.");
        }

        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Raspberry",id_raspberry.toString(),null,"Update Raspberry: ",EnumAction.UPDATE);


        if (dados.raspberry_name() != null){
            raspberry.setId_raspberry(dados.raspberry_name());
            userlog.setField("raspberry_name");
            userlog.setDescription("user_id to: " + dados.raspberry_name());
        }

        if (dados.id_building() != null){
            var building = buildingRepository.findById(dados.id_building()).orElseThrow(() -> new NotFoundException("Building id not found"));
            raspberry.setBuilding(building);
            userlog.setField("id_building");
            userlog.setDescription("id_building to: " + dados.id_building());
        }

        if (dados.id_area() != null){
            var area = areaRepository.findById(dados.id_area()).orElseThrow(() -> new NotFoundException("Area id not found"));
            raspberry.setArea(area);
            userlog.setField("id_area");
            userlog.setDescription("id_area to: " + dados.id_area());
        }

        raspberryRepository.save(raspberry);
        userLogRepository.save(userlog);

        return new RaspberryDetailsDTO(raspberry);

    }

    @Override
    public void activeRaspberry(String id_Raspberry, Long user_id) {
        var user = userRepository.findById(user_id).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));
        var raspberry = this.raspberryRepository.findById(id_Raspberry).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));
        if (!raspberry.isOperative()){
            raspberry.setOperative(true);

            var userLog = new UserLog(user,"Raspberry",id_Raspberry.toString(),"Operative","Activated Raspberry",EnumAction.UPDATE);
            userLogRepository.save(userLog);

            raspberryRepository.save(raspberry);
        }
    }

    @Override
    public void inactivateRaspberry(String id_Raspberry, Long user_id) {

        var user = userRepository.findById(user_id).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));
        var raspberry = this.raspberryRepository.findById(id_Raspberry).orElseThrow(()->new NotFoundException("Id Enviroment Not Found"));;
        if (raspberry.isOperative()){
            raspberry.setOperative(false);

            var userLog = new UserLog(user,"Raspberry",id_Raspberry.toString(),"Operative","Inactivated Raspberry",EnumAction.UPDATE);
            userLogRepository.save(userLog);

            raspberryRepository.save(raspberry);
        }
    }
}
