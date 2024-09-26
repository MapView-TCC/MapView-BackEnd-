package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.Exceptions.BlankErrorException;
import com.MapView.BackEnd.infra.Exceptions.OperativeFalseException;
import com.MapView.BackEnd.repository.AreaRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.AreaService;
import com.MapView.BackEnd.dtos.Area.AreaCreateDTO;
import com.MapView.BackEnd.dtos.Area.AreaDetailsDTO;
import com.MapView.BackEnd.dtos.Area.AreaUpdateDTO;
import com.MapView.BackEnd.entities.Area;
import com.MapView.BackEnd.infra.Exceptions.NotFoundException;
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
    public AreaDetailsDTO getArea(Long userLog_id,Long id_area) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Area id not found"));

        Area area = this.areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Area id not found"));
        if (!area.isOperative()){
            throw new OperativeFalseException("The inactive area cannot be read..");
        }

        var userLog = new UserLog(user,"Area",id_area.toString(),"Read Area",EnumAction.READ);
        userLogRepository.save(userLog);
        return new AreaDetailsDTO(area);
    }

    @Override
    public List<AreaDetailsDTO> getAllArea(int page, int itens,Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Area","Read All Area", EnumAction.READ);
        userLogRepository.save(userLog);

        return areaRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(AreaDetailsDTO::new).toList();
    }

    @Override
    public AreaDetailsDTO createArea(AreaCreateDTO data, Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var area = new Area(data);
        Long id_area = areaRepository.save(area).getId_area();

        var userLog = new UserLog(user,"Area",id_area.toString(),"Create new Area", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new AreaDetailsDTO(area);
    }

    @Override
    public AreaDetailsDTO updateArea(Long id_area, AreaUpdateDTO data,Long userLog_id) {
        var area = areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Id not found"));
        if(!area.isOperative()){
            throw new OperativeFalseException("The inactive area cannot be updated.");
        }

        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Area", id_area.toString(),null,"Infos update",EnumAction.UPDATE);


        if (data.area_name() != null){
            if(data.area_code().isBlank()){
                throw new BlankErrorException("Area name cannot not be blank");
            }
            area.setArea_name(data.area_name());
            userlog.setField("area_name to: "+ data.area_name());
        }
        if (data.area_code() != null){
            if(data.area_code().isBlank()){
                throw new BlankErrorException("Area code cannot not be blank");
            }
            area.setArea_code(data.area_code());
            userlog.setField(userlog.getField()+" ,"+"area_code to: "+data.area_code());
        }
        areaRepository.save(area);

        userLogRepository.save(userlog);
        return new AreaDetailsDTO(area);
    }

    @Override
    public void activateArea(Long id_area,Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var area = areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Id not found"));
        area.setOperative(true);
        areaRepository.save(area);

        var userLog = new UserLog(user,"Area",id_area.toString(),"Operative","Activated Area",EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateArea(Long id_area,Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var area = areaRepository.findById(id_area).orElseThrow(() -> new NotFoundException("Id not found"));
        area.setOperative(false);
        areaRepository.save(area);

        var userLog = new UserLog(user,"Area",id_area.toString(),"Operative","Inactivated Area",EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }
}
