package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Classes.ClassesCreateDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesDetaiLDTO;
import com.MapView.BackEnd.dtos.CostCenter.CostCenterCreateDTO;
import com.MapView.BackEnd.dtos.CostCenter.CostCenterDetailsDTO;
import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.Equipment.EquipmentDetailsDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationDetalsDTO;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerCreateDTO;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerDetailsDTO;
import com.MapView.BackEnd.dtos.Post.PostCreateDTO;
import com.MapView.BackEnd.dtos.Post.PostDetailDTO;
import com.MapView.BackEnd.dtos.Register.RegisterCreateDTO;
import com.MapView.BackEnd.dtos.Register.RegisterDetailsDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleCrateDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleDetailsDTO;
import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.NotFoundException;
import com.MapView.BackEnd.infra.PostsErrorException;
import com.MapView.BackEnd.repository.*;
import com.MapView.BackEnd.service.RegisterService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private final CostCenterServiceImp costCenterServiceImp;
    private final ClassesServiceImp classesServiceImp;
    private final ResponsibleServiceImp responsibleServiceImp;
    private  final EquipmentResponsibleServiceImp equipmentResponsibleServiceImp;


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
                              LocationServiceImp locationServiceImp,
                              PostServiceImp postServiceImp,
                              EnviromentServiceImp enviromentServiceImp,
                              MainOwnerServiceImp mainOwnerServiceImp,
                              CostCenterServiceImp costCenterServiceImp,
                              ClassesServiceImp classesServiceImp,
                              ResponsibleServiceImp responsibleServiceImp, EquipmentResponsibleServiceImp equipmentResponsibleServiceImp) {

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
        this.costCenterServiceImp = costCenterServiceImp;
        this.classesServiceImp = classesServiceImp;
        this.responsibleServiceImp = responsibleServiceImp;
        this.equipmentResponsibleServiceImp = equipmentResponsibleServiceImp;
    }


    @Override
    public RegisterDetailsDTO register(RegisterCreateDTO data,Long userLog_id) {
        Users userlog = userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("This uses is incorrect"));

        try {
            PostDetailDTO post = postServiceImp.createPost(new PostCreateDTO(data.post()),userLog_id);
            LocationDetalsDTO location = locationServiceImp.createLocation(new LocationCreateDTO(post.id_post(),data.id_eviroment()));
            CostCenterDetailsDTO costcenter = costCenterServiceImp.createCostCenter(new CostCenterCreateDTO(data.costCenter_name()),userLog_id);

            MainOwnerDetailsDTO owner = mainOwnerServiceImp.createMainOwner(new MainOwnerCreateDTO(data.id_owner(),costcenter.id_cost_center()),userLog_id);
            EquipmentDetailsDTO equipment = equipmentServiceImp.createEquipment(new EquipmentCreateDTO(
                    data.id_equipment(),
                    data.name_equipment(),
                    data.rfid(), data.type(),
                    data.model(),data.validity(),
                    data.admin_rights(),
                    data.observation(),
                    location.id_location(),
                    owner.id_owner()),
                    userLog_id);

            ClassesDetaiLDTO newClasses = classesServiceImp.createClasses(new ClassesCreateDTO(data.enumCourse(), data.name_classes(), userLog_id,LocalDate.now()),userLog_id);
            List<ResponsibleDetailsDTO> responsibleDetailsDTO = new ArrayList<>();

                for (ResponsibleCrateDTO listResponsible: data.dataResposible()) {
                    ResponsibleDetailsDTO responsible = responsibleServiceImp.createResposible(new ResponsibleCrateDTO(
                            listResponsible.responsible_name(),
                            listResponsible.edv(),
                            newClasses.id_classes(),
                            userLog_id), userLog_id);

                    responsibleDetailsDTO.add(responsible);
                    EquipmentResponsibleDetailsDTO equipmentResponsible = equipmentResponsibleServiceImp.createEquipmentResponsible(new EquipmentResponsibleCreateDTO(equipment.id_equipment(),responsible.responsible_id(),LocalDate.now(),LocalDate.now()));
                    UserlogCreate(userlog,"EquipmentResponsible",equipmentResponsible.id_equip_resp().toString(),"Create new EquipmentResponsible");

                }

            return new RegisterDetailsDTO(equipment,location,responsibleDetailsDTO);



        }catch (Exception e){
            throw new PostsErrorException("Um dos dos posts falharam");
        }




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


