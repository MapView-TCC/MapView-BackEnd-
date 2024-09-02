package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.repository.AreaRepository;
import com.MapView.BackEnd.repository.BuildingRepository;
import com.MapView.BackEnd.repository.RaspberryRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.service.RaspberryService;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryCreateDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryDetailsDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryUpdateDTO;
import com.MapView.BackEnd.entities.Area;
import com.MapView.BackEnd.entities.Building;
import com.MapView.BackEnd.entities.Raspberry;
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

    public RaspberryServiceImp(RaspberryRepository raspberryRepository, AreaRepository areaRepository, BuildingRepository buildingRepository, UserLogRepository userLogRepository) {
        this.raspberryRepository = raspberryRepository;
        this.areaRepository = areaRepository;
        this.buildingRepository = buildingRepository;
        this.userLogRepository = userLogRepository;
    }


    @Override
    public RaspberryDetailsDTO getRaspberry(Long id_Raspberry, Long user_id) {
        Raspberry raspberry = this.raspberryRepository.findById(id_Raspberry)
                .orElseThrow(() -> new NotFoundException("Raspberry id not found"));

        if (!raspberry.status_check()){
            throw new NotFoundException("Raspberry status is not valid");
        }

        var userLog = new UserLog(null,"Raspberry",id_Raspberry,"Read Raspberry",EnumAction.READ);
        userLogRepository.save(userLog);

        return new RaspberryDetailsDTO(raspberry);
    }

    @Override
    public List<RaspberryDetailsDTO> getAllRaspberry(int page, int itens, Long user_id) {
        var userLog = new UserLog(null,"Raspberry","Read All Raspberry", EnumAction.READ);
        userLogRepository.save(userLog);

        return raspberryRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(RaspberryDetailsDTO::new).toList();
    }

    @Override
    public RaspberryDetailsDTO createRaspberry(RaspberryCreateDTO raspberryCreateDTO, Long user_id) {
        Building building = buildingRepository.findById(raspberryCreateDTO.id_building())
                .orElseThrow(() -> new NotFoundException("Building id not found."));

        Area area = areaRepository.findById(raspberryCreateDTO.id_area())
                .orElseThrow(() -> new NotFoundException("Area id not found"));

        Raspberry raspberry = new Raspberry(raspberryCreateDTO, building, area);

        Long id_raspberry = raspberryRepository.save(raspberry).getId_raspberry();

        var userLog = new UserLog(null,"Raspberry", id_raspberry,"Create new Raspberry", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new RaspberryDetailsDTO(raspberry);

    }

    @Override
    public RaspberryDetailsDTO updateRaspberry(Long id_raspberry, RaspberryUpdateDTO dados, Long user_id) {
        var raspberry = raspberryRepository.findById(id_raspberry)
                .orElseThrow(() -> new NotFoundException("Raspberry id not found"));
        var userlog = new UserLog(null,"Raspberry",id_raspberry,null,"Update Raspberry: ",EnumAction.UPDATE);


        if (dados.raspberry_name() != null){
            raspberry.setRaspberry_name(dados.raspberry_name());
            userlog.setField("raspberry_name");
            userlog.setDescription("user_id to: " + dados.raspberry_name());

        }

        if (dados.id_building() != null){
            var building = buildingRepository.findById(dados.id_building()).orElseThrow(() -> new NotFoundException("Building id not found"));
            raspberry.setId_building(building);
            userlog.setField("id_building");
            userlog.setDescription("id_building to: " + dados.id_building());
        }

        if (dados.id_area() != null){
            var area = areaRepository.findById(dados.id_area()).orElseThrow(() -> new NotFoundException("Area id not found"));
            raspberry.setId_area(area);
            userlog.setField("id_area");
            userlog.setDescription("id_area to: " + dados.id_area());
        }

        raspberryRepository.save(raspberry);
        userLogRepository.save(userlog);

        return new RaspberryDetailsDTO(raspberry);

    }

    @Override
    public void activeRaspberry(Long id_Raspberry, Long user_id) {
        var raspberryClass = this.raspberryRepository.findById(id_Raspberry);
        if (raspberryClass.isPresent()){
            var raspberry = raspberryClass.get();
            raspberry.setOperative(true);
        }

        var userLog = new UserLog(null,"Raspberry",id_Raspberry,"Operative","Activated Raspberry",EnumAction.UPDATE);
        userLogRepository.save(userLog);

    }

    @Override
    public void inactivateRaspberry(Long id_Raspberry, Long user_id) {
        var raspberryClass = this.raspberryRepository.findById(id_Raspberry);
        if (raspberryClass.isPresent()){
            var raspberry = raspberryClass.get();
            raspberry.setOperative(false);
        }

        var userLog = new UserLog(null,"Raspberry",id_Raspberry,"Operative","Inactivated Raspberry",EnumAction.UPDATE);
        userLogRepository.save(userLog);

    }
}
