package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Equipment.EquipmentUpdateDTO;
import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.infra.NotFoundException;
import com.MapView.BackEnd.repository.*;
import com.MapView.BackEnd.service.EquipmentService;
import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.Equipment.EquipmentDetailsDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImp implements EquipmentService {


    private final EquipmentRepository equipmentRepository ;
    private final LocationRepository locationRepository;
    private final MainOwnerRepository mainOwnerRepository;
    private final UserLogRepository userLogRepository;
    private final UserRepository userRepository;

    public EquipmentServiceImp(EquipmentRepository equipmentRepository, LocationRepository locationRepository, MainOwnerRepository mainOwnerRepository,
                               UserLogRepository userLogRepository, UserRepository userRepository) {
        this.equipmentRepository = equipmentRepository;
        this.locationRepository = locationRepository;
        this.mainOwnerRepository = mainOwnerRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
    }


    @Override
    public EquipmentDetailsDTO getEquipment(String id_equipment, Long user_id) {
        var equipment = equipmentRepository.findById(String.valueOf(id_equipment)).orElseThrow(() -> new NotFoundException("Id equipment not found!"));
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        if (!equipment.status_check()){
            return null;
        }

        var userLog = new UserLog(user, "Equipment", id_equipment, "Read Equipment", EnumAction.READ);
        userLogRepository.save(userLog);
        return new EquipmentDetailsDTO(equipment);
    }

    @Override
    public List<EquipmentDetailsDTO> getAllEquipment(int page, int itens, Long user_id) {
        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Equipment","Read All Equipment", EnumAction.READ);
        userLogRepository.save(userLog);

        return equipmentRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(EquipmentDetailsDTO::new).toList();
    }


    @Override
    public EquipmentDetailsDTO createEquipment(EquipmentCreateDTO dados, Long user_id) {
        Users users = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found!"));

        // location
        Location location = locationRepository.findById(Long.valueOf(dados.id_location()))
                .orElseThrow(() -> new RuntimeException("Id location Não encontrado!"));

        // main owner
        MainOwner mainOwner = mainOwnerRepository.findById(String.valueOf(dados.id_owner()))
                .orElseThrow(() -> new RuntimeException("Id main owner Não encontrado"));


        Equipment equipment = new Equipment(dados,location,mainOwner);

        equipmentRepository.save(equipment);

        var userLog = new UserLog(users,"Equipment", dados.id_equipment(), "Create new Equipment", EnumAction.CREATE);
        userLogRepository.save(userLog);

        return new EquipmentDetailsDTO(equipment);
    }

    @Override
    public EquipmentDetailsDTO updateEquipment(String id_equipment, EquipmentUpdateDTO dados, Long user_id) {
        var equipment = equipmentRepository.findById(id_equipment)
                .orElseThrow(() -> new NotFoundException("Id not found"));

        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Equipment",dados.id_equipment(),null,"Infos update",EnumAction.UPDATE);

        if (dados.name_equipment() != null){
            equipment.setName_equipment(dados.name_equipment());
            userlog.setField("equipment to: " + dados.name_equipment());
        }

        if (dados.rfid() != null) {
            equipment.setRfid(dados.rfid());
            userlog.setField(userlog.getField() + "equipment to: " + dados.rfid());
        }

        if (dados.type() != null) {
            equipment.setType(dados.type());
            userlog.setField(userlog.getField() + "equipment to: " + dados.type());
        }

        if (dados.model() != null) {
            equipment.setModel(dados.model());
            userlog.setField(userlog.getField() + "equipment to: " + dados.model());
        }

        if (dados.validity() != null) {
            equipment.setValidity(dados.validity());
            userlog.setField(userlog.getField() + "equipment to: " + dados.validity());
        }

        if (dados.admin_rights() != null) {
            equipment.setAdmin_rights(dados.admin_rights());
            userlog.setField(userlog.getField() + "equipment to: " + dados.admin_rights());
        }

        if (dados.observation() != null) {
            equipment.setObservation(dados.observation());
            userlog.setField(userlog.getField() + "equipment to: " + dados.observation());
        }

        if (dados.id_location() != null) {
            var location = locationRepository.findById(dados.id_location())
                    .orElseThrow(() -> new NotFoundException("Location id not found"));
            equipment.setId_location(location);
            userlog.setField(userlog.getField() + "equipment to: " + dados.id_location());
        }

        if (dados.id_owner() != null) {
            var owner = mainOwnerRepository.findById(dados.id_owner())
                    .orElseThrow(() -> new NotFoundException("Owner id not found"));
            equipment.setId_owner(owner);
            userlog.setField(userlog.getField() + "equipment to: " + dados.id_owner());
        }

        userLogRepository.save(userlog);

        // Salva a entidade atualizada no repositório
        equipmentRepository.save(equipment);
        return new EquipmentDetailsDTO(equipment);
    }

    @Override
    public void activateEquipment(String id_equipment, Long user_id) {
        Users users = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var equipmentClass = equipmentRepository.findById(id_equipment);
        if (equipmentClass.isPresent()){
            var equipment = equipmentClass.get();
            equipment.setOperative(true);
        }

        var userLog = new UserLog(users, "Equipment", id_equipment, "Operative", "Activated area", EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateEquipment(String id_equipment, Long user_id) {
        Users users = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));

        var equipmentClass = equipmentRepository.findById(id_equipment);
        if (equipmentClass.isPresent()){
            var equipment = equipmentClass.get();
            equipment.setOperative(false);
        }

        var userLog = new UserLog(users, "Equipment", id_equipment, "Operative", "Inactivated area", EnumAction.UPDATE);
        userLogRepository.save(userLog);
    }

    @Override
    public List<EquipmentDetailsDTO> getEquipmentValidation(int page, int itens, String validity,
                                                            String environment, String mainOwner,
                                                            String id_owner, String id_equipment,
                                                            String name_equipment, String post) {

        List<Equipment> filteredEquipments = equipmentRepository.findAllByOperativeTrue(PageRequest.of(page, itens))
                .stream()
                .filter(e -> (validity == null || e.getValidity().equals(validity)) &&
                        (environment == null || e.getId_location().getEnvironment().getEnvironment_name().equals(environment)) &&
                        (mainOwner == null || e.getId_owner().getOwner_name().equals(mainOwner)) &&
                        (id_owner == null || e.getId_owner().getId_owner().equals(id_owner)) &&
                        (id_equipment == null || e.getId_equipment().equals(id_equipment)) &&
                        (name_equipment == null || e.getName_equipment().equals(name_equipment)) &&
                        (post == null || e.getId_location().getPost().getPost().equals(post)))
                .toList();


        if (validity == null && environment == null && mainOwner == null && id_owner == null && id_equipment == null &&
                name_equipment == null && post == null) {
            return equipmentRepository.findAllByOperativeTrue(PageRequest.of(page, itens))
                    .stream()
                    .map(EquipmentDetailsDTO::new)
                    .collect(Collectors.toList());
        }


        return filteredEquipments.stream()
                .map(EquipmentDetailsDTO::new)
                .collect(Collectors.toList());
    }


}
