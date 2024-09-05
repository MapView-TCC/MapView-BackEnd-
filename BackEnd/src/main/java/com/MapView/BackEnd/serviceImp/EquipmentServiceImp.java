package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Equipment.EquipmentUpdateDTO;
import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import com.MapView.BackEnd.infra.NotFoundException;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.repository.*;
import com.MapView.BackEnd.service.EquipmentService;
import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.Equipment.EquipmentDetailsDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImp implements EquipmentService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final EquipmentRepository equipmentRepository ;
    private final LocationRepository locationRepository;
    private final MainOwnerRepository mainOwnerRepository;
    private final UserLogRepository userLogRepository;
    private final UserRepository userRepository;
    private final Path fileStorageLocation;


    public EquipmentServiceImp(EntityManager entityManager, EquipmentRepository equipmentRepository, LocationRepository locationRepository, MainOwnerRepository mainOwnerRepository,
                               UserLogRepository userLogRepository, UserRepository userRepository, FileStorageProperties fileStorageProperties) {
        this.entityManager = entityManager;
        this.equipmentRepository = equipmentRepository;
        this.locationRepository = locationRepository;
        this.mainOwnerRepository = mainOwnerRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
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

        if(!equipment.isOperative()){
            throw new OperativeFalseException("The inactive equipment cannot be updated.");
        }

        Users user = this.userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Equipment",dados.id_equipment(),null,"Infos update",EnumAction.UPDATE);

        if (dados.id_equipment() != null){
            equipment.setId_equipment(dados.id_equipment());
            userlog.setField("equipment id to: " + dados.id_equipment());
        }

        if (dados.name_equipment() != null){
            equipment.setName_equipment(dados.name_equipment());
            userlog.setField(userlog.getField()+" ,"+"equipment name to: " + dados.name_equipment());
        }

        if (dados.rfid() != null) {
            equipment.setRfid(dados.rfid());
            userlog.setField(userlog.getField()+" ,"+"equipment rfid to: " + dados.rfid());
        }

        if (dados.type() != null) {
            equipment.setType(dados.type());
            userlog.setField(userlog.getField()+" ,"+"equipment type to: " + dados.type());
        }

        if (dados.model() != null) {
            equipment.setModel(dados.model());
            userlog.setField(userlog.getField()+" ,"+"equipment model to: " + dados.model());
        }

        if (dados.validity() != null) {
            equipment.setValidity(dados.validity());
            userlog.setField(userlog.getField()+" ,"+"equipment validity to: " + dados.validity());
        }

        if (dados.admin_rights() != null) {
            equipment.setAdmin_rights(dados.admin_rights());
            userlog.setField(userlog.getField()+" ,"+"equipment admin rights to: " + dados.admin_rights());
        }

        if (dados.observation() != null) {
            equipment.setObservation(dados.observation());
            userlog.setField(userlog.getField()+" ,"+"equipment observation to: " + dados.observation());
        }

        if (dados.id_location() != null) {
            var location = locationRepository.findById(dados.id_location())
                    .orElseThrow(() -> new NotFoundException("Location id not found"));
            equipment.setLocation(location);
            userlog.setField(userlog.getField()+" ,"+"equipment location to: " + dados.id_location());
        }

        if (dados.id_owner() != null) {
            var owner = mainOwnerRepository.findById(dados.id_owner())
                    .orElseThrow(() -> new NotFoundException("Owner id not found"));
            equipment.setOwner(owner);
            userlog.setField(userlog.getField()+" ,"+"equipment main owner to: " + dados.id_owner());
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
                                                            String idOwner, String idEquipment,
                                                            String nameEquipment, String post) {


        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Equipment> criteriaQuery = criteriaBuilder.createQuery(Equipment.class);

        // Select From Equipment
        Root<Equipment> equipmentRoot = criteriaQuery.from(Equipment.class);

        // Join with related entities
        Join<Equipment, Location> locationJoin = equipmentRoot.join("location");
        Join<Location, Post> postJoin = locationJoin.join("post");
        Join<Location, Enviroment   > environmentJoin = locationJoin.join("environment");
        Join<Equipment, MainOwner> mainOwnerJoin = equipmentRoot.join("owner");

        // List of predicates for the WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // WHERE clauses based on parameters
        if (validity != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(equipmentRoot.get("validity")),
                    "%" + validity.toLowerCase() + "%"));
        }
        if (environment != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(environmentJoin.get("environment_name")),
                    "%" + environment.toLowerCase() + "%"));
        }
        if (mainOwner != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(mainOwnerJoin.get("owner_name")),
                    "%" + mainOwner.toLowerCase() + "%"));
        }
        if (idOwner != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(equipmentRoot.get("id_owner")),
                    "%" + idOwner.toLowerCase() + "%"));
        }
        if (idEquipment != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(equipmentRoot.get("id_equipment")),
                    "%" + idEquipment.toLowerCase() + "%"));
        }
        if (nameEquipment != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(equipmentRoot.get("name_equipment")),
                    "%" + nameEquipment.toLowerCase() + "%"));
        }
        if (post != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(postJoin.get("post")),
                    "%" + post.toLowerCase() + "%"));
        }

        // Apply predicates to the query
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Create a query and set pagination
        TypedQuery<Equipment> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(page * itens);
        query.setMaxResults(itens);

        List<Equipment> resultList = query.getResultList();

        // Convert results to DTOs
        return resultList.stream()
                .map(EquipmentDetailsDTO::new)
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> uploadImageEquipament (MultipartFile file,EnumModelEquipment equipment){
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            file.transferTo(targetLocation);

            equipament_image(targetLocation,equipment);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok("File uploaded successfully. Download link: " + fileDownloadUri);
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("File upload failed.");
        }

    }

    public void equipament_image(Path targetLocation, EnumModelEquipment equipmentModel){
        String targetLocatioString = targetLocation.toString();

        List<Equipment> allEquipments = equipmentRepository.findByModel(equipmentModel);

        for (Equipment equipment : allEquipments) {
            equipment.setImage(targetLocatioString);
        }

        equipmentRepository.saveAll(allEquipments);
    }


}
