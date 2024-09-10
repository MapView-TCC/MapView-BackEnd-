package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.AreaRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.AreaService;
import com.MapView.BackEnd.dtos.Area.AreaCreateDTO;
import com.MapView.BackEnd.dtos.Area.AreaDetailsDTO;
import com.MapView.BackEnd.dtos.Area.AreaUpdateDTO;
import com.MapView.BackEnd.entities.Area;
import com.MapView.BackEnd.infra.NotFoundException;
import com.MapView.BackEnd.service.UserLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaServiceImp implements AreaService {


    private final AreaRepository areaRepository;
    private final UserLogRepository userLogRepository;
    private final UserRepository userRepository;

    public AreaServiceImp(AreaRepository areaRepository, UserLogService userLogService, UserLogRepository userLogRepository, UserRepository userRepository) {
        this.areaRepository = areaRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AreaDetailsDTO getArea(Long user_id,Long id_area) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Area id not found"));

        Area area = this.areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Area id not found"));
        if (!area.isOperative()){
            throw new OperativeFalseException("The inactive area cannot be read..");
        }

        var userLog = new UserLog(user,"Area",id_area.toString(),"Read Area",EnumAction.READ);
        userLogRepository.save(userLog);
        return new AreaDetailsDTO(area);
    }

    @Override
    public List<AreaDetailsDTO> getAllArea(int page, int itens,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Area","Read All Area", EnumAction.READ);
        userLogRepository.save(userLog);

        return areaRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(AreaDetailsDTO::new).toList();
    }

    @Override
    public AreaDetailsDTO createArea(AreaCreateDTO dados, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var area = new Area(dados);
        Long id_area = areaRepository.save(area).getId_area();

        var userLog = new UserLog(user,"Area",id_area.toString(),"Create new Area", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new AreaDetailsDTO(area);
    }

    @Override
    public AreaDetailsDTO updateArea(Long id_area, AreaUpdateDTO dados,Long user_id) {
        var area = areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Id not found"));
        if(!area.isOperative()){
            throw new OperativeFalseException("The inactive area cannot be updated.");
        }

        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Area", id_area.toString(),null,"Infos update",EnumAction.UPDATE);


        if (dados.area_name() != null){
            area.setArea_name(dados.area_name());
            userlog.setField("area_name to: "+ dados.area_name());
        }
        if (dados.area_code() != null){
            area.setArea_code(dados.area_code());
            userlog.setField(userlog.getField()+" ,"+"area_code to: "+dados.area_code());
        }
        areaRepository.save(area);

        userLogRepository.save(userlog);
        return new AreaDetailsDTO(area);
    }

    @Override
    public void activateArea(Long id_area,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var area = areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Id not found"));
        area.setOperative(true);
        areaRepository.save(area);

        var userLog = new UserLog(user,"Area",id_area.toString(),"Operative","Activated Area",EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateArea(Long id_area,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var area = areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Id not found"));
        area.setOperative(false);
        areaRepository.save(area);

        var userLog = new UserLog(user,"Area",id_area.toString(),"Operative","Inactivated Area",EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }
}
