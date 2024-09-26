package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.RegisterDetailsDTO;
import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.Exceptions.NotFoundException;
import com.MapView.BackEnd.infra.Exceptions.OperativeFalseException;
import com.MapView.BackEnd.repository.*;
import com.MapView.BackEnd.service.RegisterService;
import org.springframework.stereotype.Service;

@Service

public class RegisterServiceImp implements RegisterService {

    private final PostRepository postRepository;
    private final EnviromentRepository enviromentRepository;
    private final EquipmentRepository equipmentRepository;
    private final LocationRepository locationRepository;
    private final UserLogRepository userLogRepository;
    private  final UserRepository userRepository;
    private final MainOwnerRepository mainOwnerRepository;
    private final ResponsibleRepository responsibleRepository;
    private final EquipmentResponsibleRepository equipmentResponsibleRepository;

    public RegisterServiceImp(PostRepository postRepository, EnviromentRepository enviromentRepository, EquipmentRepository equipmentRepository, LocationRepository locationRepository, UserLogRepository userLogRepository, UserRepository userRepository, MainOwnerRepository mainOwnerRepository, ResponsibleRepository responsibleRepository, EquipmentResponsibleRepository equipmentResponsibleRepository) {
        this.postRepository = postRepository;
        this.enviromentRepository = enviromentRepository;
        this.equipmentRepository = equipmentRepository;
        this.locationRepository = locationRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
        this.mainOwnerRepository = mainOwnerRepository;
        this.responsibleRepository = responsibleRepository;
        this.equipmentResponsibleRepository = equipmentResponsibleRepository;
    }


    @Override
    public RegisterDetailsDTO register(EquipmentCreateDTO dataEquipment, LocationCreateDTO datalocation, EquipmentResponsibleCreateDTO dataResponsible,Long userLog_id) {
        Users user = userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("This uses is incorrect"));

        // location
        Location locationEquip = locationRepository.findById(Long.valueOf(dataEquipment.id_location()))
                .orElseThrow(() -> new RuntimeException("Id location Não encontrado!"));

        // main owner
        MainOwner mainOwner = mainOwnerRepository.findById(String.valueOf(dataEquipment.id_owner()))
                .orElseThrow(() -> new RuntimeException("Id main owner Não encontrado"));

        if(!mainOwner.isOperative()){
            throw new OperativeFalseException("The inactive mainowner cannot be accessed.");
        }

        var post = postRepository.findById(datalocation.id_post()).orElseThrow(() -> new NotFoundException("Post Id not Found"));
        if(!post.isOperative()){
            throw new OperativeFalseException("The inactive post cannot be accessed.");
        }

        var enviroment = enviromentRepository.findById(datalocation.id_eviroment()).orElseThrow(() -> new NotFoundException("Enviroment Id Not Found"));
        if(!enviroment.isOperative()){
            throw new OperativeFalseException("The inactive enviroment cannot be accessed.");
        }

        Responsible responsible = responsibleRepository.findById(dataResponsible.id_responsible())
                .orElseThrow(() -> new NotFoundException("Responsible id not found"));


        Equipment equipment = equipmentRepository.save(new Equipment(dataEquipment,locationEquip,mainOwner));
        UserlogCreate(user,"Equipment",equipment.getId_equipment(),"Create new Equipment");

        Location location = locationRepository.save(new Location(post,enviroment));
        UserlogCreate(user,"Location",location.getId_location().toString(),"Create new Location");

        EquipmentResponsible equipmentResponsible = equipmentResponsibleRepository.save(new EquipmentResponsible(dataResponsible, equipment, responsible));
        UserlogCreate(user,"EquipmentResponsible",equipmentResponsible.getId_equip_resp().toString(),"Create new EquipmentResponsible");

        return new RegisterDetailsDTO(equipment,location,equipmentResponsible);
    }

    public void UserlogCreate(Users user, String tabela, String id,String descrption){

        var userLog = new UserLog(user,tabela,id,descrption, EnumAction.CREATE);
        userLogRepository.save(userLog);

    }
}


