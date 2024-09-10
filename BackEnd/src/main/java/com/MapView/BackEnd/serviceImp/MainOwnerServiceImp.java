package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.MainOwner.MainOwnerCreateDTO;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerDetailsDTO;
import com.MapView.BackEnd.entities.UserLog;
import com.MapView.BackEnd.entities.Users;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.CostCenterRepository;
import com.MapView.BackEnd.repository.MainOwnerRepository;
import com.MapView.BackEnd.repository.UserLogRepository;
import com.MapView.BackEnd.repository.UserRepository;
import com.MapView.BackEnd.service.MainOwnerService;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerUpdateDTO;
import com.MapView.BackEnd.entities.CostCenter;
import com.MapView.BackEnd.entities.MainOwner;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainOwnerServiceImp implements MainOwnerService {


    private final MainOwnerRepository mainOwnerRepository;


    private final CostCenterRepository costCenterRepository;
    private final UserRepository userRepository;
    private final UserLogRepository userLogRepository;

    public MainOwnerServiceImp(MainOwnerRepository mainOwnerRepository, CostCenterRepository costCenterRepository, UserRepository userRepository, UserLogRepository userLogRepository) {
        this.mainOwnerRepository = mainOwnerRepository;
        this.costCenterRepository = costCenterRepository;
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
    }

    @Override
    public MainOwnerDetailsDTO getMainOwner(String id_owner,Long user_id) {
        var mainOwner = this.mainOwnerRepository.findById(id_owner).orElseThrow(() -> new NotFoundException("Id not found"));
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        if (!mainOwner.isOperative()){
            throw new NotFoundException("The inactive mainowner cannot be accessed.");
        }

        var userLog = new UserLog(user,"MainOwner",id_owner.toString(),"Read MainOwner",EnumAction.READ);
        userLogRepository.save(userLog);

        return new MainOwnerDetailsDTO(mainOwner);
    }

    @Override
    public List<MainOwnerDetailsDTO> getAllMainOwner(int page, int itens,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"MainOwner","Read All MainOwner", EnumAction.READ);
        userLogRepository.save(userLog);


        return mainOwnerRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(MainOwnerDetailsDTO::new).toList();
    }

    @Override
    public MainOwnerDetailsDTO createMainOwner(MainOwnerCreateDTO mainOwnerDTO,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        CostCenter costCenter = costCenterRepository.findById(mainOwnerDTO.id_cost_center())
                .orElseThrow(() -> new RuntimeException("Não encontrado!"));

        MainOwner mainOwner = new MainOwner(mainOwnerDTO, costCenter);
        String mainowner_id = mainOwnerRepository.save(mainOwner).getId_owner();

        var userLog = new UserLog(user,"MainOwner",mainowner_id.toString(),"Create new MainOwner", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new MainOwnerDetailsDTO(mainOwner);

    }

    @Override
    public MainOwnerDetailsDTO updateMainOwner(String id_owner, MainOwnerUpdateDTO dados,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var mainowner = mainOwnerRepository.findById(id_owner).orElseThrow(() -> new NotFoundException("Main Owner id not found"));

        if(!mainowner.isOperative()){
            throw new OperativeFalseException("The inactive equipment cannot be updated.");
        }


        var userlog = new UserLog(user,"Area", id_owner.toString(),null,"Infos update", EnumAction.UPDATE);

        if (!mainowner.isOperative()){
            return null;
        }
        if (dados.owner_name() != null){
            mainowner.setOwner_name(dados.owner_name());
            userlog.setField("Main Owner name to: "+dados.owner_name());
        }

        if (dados.id_cost_center() != null){
            var costcenter = costCenterRepository.findById(dados.id_cost_center()).orElseThrow(() -> new NotFoundException("Cost Center id not found"));
            mainowner.setCostCenter(costcenter);
            userlog.setField(userlog.getField()+"Cost_id to: "+dados.id_cost_center());
        }

        mainOwnerRepository.save(mainowner);
        userLogRepository.save(userlog);
        return new MainOwnerDetailsDTO(mainowner);
    }

    @Override
    public void activateMainOwner(String id_owner,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var mainowner = this.mainOwnerRepository.findById(id_owner).orElseThrow(() -> new NotFoundException("Cost Center id not found"));
        mainowner.setOperative(true);
        mainOwnerRepository.save(mainowner);

        var userLog = new UserLog(user,"MainOwner",id_owner,"Operative","Activated MainOwner",EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateMainOwner(String id_owner,Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var mainowner = this.mainOwnerRepository.findById(id_owner).orElseThrow(()-> new NotFoundException("id NOT FOUND"));
        mainowner.setOperative(false);
        mainOwnerRepository.save(mainowner);

        var userLog = new UserLog(user,"MainOwner",id_owner,"Operative","Inactivated MainOwner",EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }
}
