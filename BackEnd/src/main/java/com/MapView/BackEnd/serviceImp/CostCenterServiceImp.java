package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.CostCenter.CostCenterCreateDTO;
import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.infra.OpetativeTrueException;
import com.MapView.BackEnd.repository.CostCenterRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.CostCenterService;
import com.MapView.BackEnd.dtos.CostCenter.CostCenterDetailsDTO;
import com.MapView.BackEnd.dtos.CostCenter.CostCenterUpdateDTO;
import com.MapView.BackEnd.entities.CostCenter;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostCenterServiceImp implements CostCenterService {


    private  final CostCenterRepository costCenterRepository;
    private final UserLogRepository userLogRepository;
    private  final UserRepository userRepository;

    public CostCenterServiceImp(CostCenterRepository costCenterRepository, UserLogRepository userLogRepository, UserRepository userRepository) {
        this.costCenterRepository = costCenterRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CostCenterDetailsDTO getCostCenter(Long id_cost_center,Long userLog_id) {
        CostCenter costCenter = this.costCenterRepository.findById(id_cost_center).orElseThrow(() -> new NotFoundException("Id not found"));
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));

        if (!costCenter.status_check()){
            throw new OperativeFalseException("The inactive CostCenter cannot be read..");
        }
        var userLog = new UserLog(user,"CostCenter",id_cost_center.toString(),"Read CostCenter", EnumAction.READ);
        userLogRepository.save(userLog);
        return new CostCenterDetailsDTO(costCenter);
    }

    @Override
    public List<CostCenterDetailsDTO> getAllCostCenter(int page,int itens,Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"CostCenter","Read All CostCenter", EnumAction.READ);
        userLogRepository.save(userLog);

        return costCenterRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(CostCenterDetailsDTO::new).toList();
    }

    @Override
    public CostCenterDetailsDTO createCostCenter(CostCenterCreateDTO dados,Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var costcenter = new CostCenter(dados);
        Long cost_id = costCenterRepository.save(costcenter).getId_cost_center();

        var userLog = new UserLog(user,"CostCenter",cost_id.toString(),"Create new CostCenter", EnumAction.CREATE);
        userLogRepository.save(userLog);
        System.out.println("Post: createCostCenter ");

        return new CostCenterDetailsDTO(costcenter);
    }

    @Override
    public CostCenterDetailsDTO updateCostCenter(Long id, CostCenterUpdateDTO dados,Long userLog_id) {
        var costCenter =  costCenterRepository.findById(id).orElseThrow(() -> new NotFoundException("Cost Center Id Not Found"));

        if(!costCenter.isOperative()){
            throw new OperativeFalseException("The inactive equipment cannot be updated.");
        }

        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Area", id.toString(),null,"Infos update",EnumAction.UPDATE);

        if (dados.costCenter_name() != null){
            costCenter.setCost_center_name(dados.costCenter_name());
            userlog.setField("const_center_name to: "+ dados.costCenter_name());
        }

        costCenterRepository.save(costCenter);
        userLogRepository.save(userlog);
        return new CostCenterDetailsDTO(costCenter);
    }

    // funções para ativer e inativar
    @Override
    public void activateCostCenter(Long id_cost_center,Long userLog_id) {

        var costCenter = costCenterRepository.findById(id_cost_center).orElseThrow(() -> new NotFoundException("Id not found"));
        if(costCenter.isOperative()){
            throw new OpetativeTrueException("It is already activate");
        }
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        costCenter.setOperative(true);
        var userLog = new UserLog(user,"CostCenter",id_cost_center.toString(),"Operative","Inactivated Cost Center",EnumAction.UPDATE);
        costCenterRepository.save(costCenter);
        userLogRepository.save(userLog);



    }

    @Override
    public void inactivateCostCenter(Long id_cost_center,Long userLog_id) {
        var costCenter = costCenterRepository.findById(id_cost_center).orElseThrow(() -> new NotFoundException("Const Center Not Found"));
        if(!costCenter.isOperative()){
            throw new OperativeFalseException("It is already inactivate");
        }
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        costCenter.setOperative(false);
        var userLog = new UserLog(user,"Cost Center",id_cost_center.toString(),"Operative","Inactivated Area",EnumAction.UPDATE);
        costCenterRepository.save(costCenter);
        userLogRepository.save(userLog);

    }
}
