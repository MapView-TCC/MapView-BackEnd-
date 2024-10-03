package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.Equipment.EquipmentDetailsDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.Register.RegisterCreateDTO;
import com.MapView.BackEnd.dtos.Register.RegisterDetailsDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleCrateDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleDetailsDTO;
import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.NotFoundException;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.*;
import com.MapView.BackEnd.service.RegisterService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final ClassesRepository classesRepository;

    private final LocationServiceImp locationServiceImp;


    public RegisterServiceImp(PostRepository postRepository,
                              EnviromentRepository enviromentRepository,
                              EquipmentRepository equipmentRepository,
                              LocationRepository locationRepository,
                              UserLogRepository userLogRepository,
                              UserRepository userRepository,
                              MainOwnerRepository mainOwnerRepository,
                              ResponsibleRepository responsibleRepository,
                              EquipmentResponsibleRepository equipmentResponsibleRepository,
                              ClassesRepository classesRepository,
                              LocationServiceImp locationServiceImp) {
        this.postRepository = postRepository;
        this.enviromentRepository = enviromentRepository;
        this.equipmentRepository = equipmentRepository;
        this.locationRepository = locationRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
        this.mainOwnerRepository = mainOwnerRepository;
        this.responsibleRepository = responsibleRepository;
        this.equipmentResponsibleRepository = equipmentResponsibleRepository;
        this.classesRepository = classesRepository;

        this.locationServiceImp = locationServiceImp;
    }


    @Override
    public RegisterDetailsDTO register(RegisterCreateDTO dataRegister,Long userLog_id) {
        Users userlog = userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("This uses is incorrect"));

        Location locationEquip = locationRepository.findById(dataRegister.dataEquipment().id_location())
                .orElseThrow(() -> new NotFoundException("Id location Não encontrado!"));


        MainOwner mainOwner = mainOwnerRepository.findById(String.valueOf(dataRegister.dataEquipment().id_owner()))
                .orElseThrow(() -> new NotFoundException("Id main owner Não encontrado"));


        if(!mainOwner.isOperative()){
            throw new OperativeFalseException("The inactive mainowner cannot be accessed.");
        }

        var post = postRepository.findById(dataRegister.dataLocation().id_post()).orElseThrow(() -> new NotFoundException("Post Id not Found"));
        if(!post.isOperative()){
            throw new OperativeFalseException("The inactive post cannot be accessed.");
        }

        var enviroment = enviromentRepository.findById(dataRegister.dataLocation().id_eviroment()).orElseThrow(() -> new NotFoundException("Enviroment Id Not Found"));
        if(!enviroment.isOperative()){
            throw new OperativeFalseException("The inactive enviroment cannot be accessed.");
        }

        LocationDetalsDTO location = locationRepository.findByPostAndEnvironment(post,enviroment).orElseGet(() -> locationServiceImp.createLocation(dataRegister.dataLocation()));

        UserlogCreate(userlog,"Location",location.id_location().toString(),"Create new Location");
        List<ResponsibleDetailsDTO> listResponsible = new ArrayList<>();

        LocalDate stringToDate = getStartDateFromQuarter(dataRegister.dataEquipment().validity());

        Equipment equipment = equipmentRepository.save(new Equipment(dataRegister.dataEquipment(),stringToDate,location,mainOwner));
        EquipmentDetailsDTO equipmentDetails  = new EquipmentDetailsDTO(equipment);
        UserlogCreate(userlog,"Equipment",equipment.getIdEquipment(),"Create new Equipment");

        for (ResponsibleCrateDTO r: dataRegister.dataResposible()) {

            Classes classes = classesRepository.findById(r.id_classes())
                    .orElseThrow(() -> new RuntimeException("Id classes not found"));
            Users user = userRepository.findById(r.id_user())
                    .orElseThrow(() -> new RuntimeException("Id classes not found"));

            Responsible responsible = responsibleRepository.findByEdv(r.edv()).orElseGet(() -> responsibleRepository.save(new Responsible(r,classes,user)));

            listResponsible.add(new ResponsibleDetailsDTO(responsible));
            EquipmentResponsible equipmentResponsible = equipmentResponsibleRepository.save(new EquipmentResponsible(responsible,equipment));
            UserlogCreate(userlog,"EquipmentResponsible",equipmentResponsible.getId_equip_resp().toString(),"Create new EquipmentResponsible");

        }


        return new RegisterDetailsDTO(equipmentDetails,location,listResponsible);

    }


    public static LocalDate getStartDateFromQuarter(String quarterStr) {
        // Dividir a string em ano e trimestre
        String[] parts = quarterStr.split("\\.");
        int year = Integer.parseInt(parts[0]);
        int quarter = Integer.parseInt(parts[1].substring(1)); // Pega o número do trimestre

        // Calcular o primeiro mês do trimestre
        int month = (quarter - 1) * 3 + 1; // Q1 -> Janeiro, Q2 -> Abril, Q3 -> Julho, Q4 -> Outubro

        // Retornar a data do primeiro dia do mês do trimestre
        return LocalDate.of(year, month, 1);
    }

    public void UserlogCreate(Users user, String tabela, String id,String descrption){

        var userLog = new UserLog(user,tabela,id,descrption, EnumAction.CREATE);
        userLogRepository.save(userLog);

    }
}


