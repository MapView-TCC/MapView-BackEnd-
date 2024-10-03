package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Enviroment.EnviromentDetailsDTO;
import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.Equipment.EquipmentDetailsDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerDetailsDTO;
import com.MapView.BackEnd.dtos.Post.PostDetailDTO;
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
    private final EquipmentServiceImp equipmentServiceImp;
    private final LocationServiceImp locationServiceImp;
    private final PostServiceImp postServiceImp;
    private final EnviromentServiceImp enviromentServiceImp;
    private final MainOwnerServiceImp mainOwnerServiceImp;


    public RegisterServiceImp(PostRepository postRepository,
                              EnviromentRepository enviromentRepository,
                              EquipmentRepository equipmentRepository,
                              LocationRepository locationRepository,
                              UserLogRepository userLogRepository,
                              UserRepository userRepository,
                              MainOwnerRepository mainOwnerRepository,
                              ResponsibleRepository responsibleRepository,
                              EquipmentResponsibleRepository equipmentResponsibleRepository,
                              ClassesRepository classesRepository, EquipmentServiceImp equipmentServiceImp,
                              LocationServiceImp locationServiceImp, PostServiceImp postServiceImp, EnviromentServiceImp enviromentServiceImp, MainOwnerServiceImp mainOwnerServiceImp) {

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
        this.equipmentServiceImp = equipmentServiceImp;
        this.locationServiceImp = locationServiceImp;
        this.postServiceImp = postServiceImp;
        this.enviromentServiceImp = enviromentServiceImp;
        this.mainOwnerServiceImp = mainOwnerServiceImp;
    }


    @Override
    public RegisterDetailsDTO register(RegisterCreateDTO data,Long userLog_id) {
        Users userlog = userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("This uses is incorrect"));




        MainOwnerDetailsDTO mainOwner = mainOwnerServiceImp.getMainOwner(dataRegister.dataEquipment().id_owner(),userLog_id);
        PostDetailDTO post = postServiceImp.getPost(dataRegister.dataLocation().id_post(),userLog_id);
        EnviromentDetailsDTO environment = enviromentServiceImp.getEnviroment(dataRegister.dataLocation().id_eviroment(),userLog_id);

        Location location = locationRepository.findByIdPostAndIdEnvironment(post.id_post(),environment.id_enviroment()).orElseGet(() -> locationRepository.save(dataRegister.dataLocation()));
        List<ResponsibleDetailsDTO> listResponsible = new ArrayList<>();

        EquipmentDetailsDTO equipmentDetailsDTO = equipmentServiceImp.createEquipment()

        EquipmentDetailsDTO equipment = equipmentRepository.findByIdEquipment(dataRegister.dataEquipment().id_equipment()).orElseGet(() -> equipmentServiceImp.createEquipment(dataRegister.dataEquipment(),userLog_id));


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


        return new RegisterDetailsDTO(equipment,location,listResponsible);

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


