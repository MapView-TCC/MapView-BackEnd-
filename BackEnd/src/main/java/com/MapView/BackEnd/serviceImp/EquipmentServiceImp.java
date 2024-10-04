package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.Equipment.EquipmentUpdateDTO;
import com.MapView.BackEnd.dtos.ImageUpload.UploadCreateDTO;
import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumAction;
import com.MapView.BackEnd.enums.EnumColors;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import com.MapView.BackEnd.enums.EnumTrackingAction;
import com.MapView.BackEnd.infra.BlankErrorException;
import com.MapView.BackEnd.infra.NotFoundException;
import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.infra.OpetativeTrueException;
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
import java.time.LocalDate;
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
    private final TrackingHistoryRepository trackingHistoryRepository;
    private final ImageRepository imageRepository;


    public EquipmentServiceImp(EntityManager entityManager, EquipmentRepository equipmentRepository, LocationRepository locationRepository, MainOwnerRepository mainOwnerRepository,
                               UserLogRepository userLogRepository, UserRepository userRepository, FileStorageProperties fileStorageProperties, TrackingHistoryRepository trackingHistoryRepository, ImageRepository imageRepository) {
        this.entityManager = entityManager;
        this.equipmentRepository = equipmentRepository;
        this.locationRepository = locationRepository;
        this.mainOwnerRepository = mainOwnerRepository;
        this.userLogRepository = userLogRepository;
        this.userRepository = userRepository;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.trackingHistoryRepository = trackingHistoryRepository;
        this.imageRepository = imageRepository;
    }


    @Override
    public EquipmentDetailsDTO getEquipment(String id_equipment, Long userLog_id) {
        var equipment = equipmentRepository.findById(String.valueOf(id_equipment)).orElseThrow(() -> new NotFoundException("Id equipment not found!"));
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));

        if (!equipment.isOperative()){
            throw new OperativeFalseException("The inactive responsible cannot be accessed.");
        }

        var userLog = new UserLog(user, "Equipment", id_equipment, "Read Equipment", EnumAction.READ);
        userLogRepository.save(userLog);
        return new EquipmentDetailsDTO(equipment);
    }

    @Override
    public List<EquipmentDetailsDTO> getAllEquipment(int page, int itens, Long userLog_id) {
        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userLog = new UserLog(user,"Equipment","Read All Equipment", EnumAction.READ);
        userLogRepository.save(userLog);

        return equipmentRepository.findAllByOperativeTrue(PageRequest.of(page, itens)).stream().map(EquipmentDetailsDTO::new).toList();
    }


    @Override
    public EquipmentDetailsDTO createEquipment(EquipmentCreateDTO data, Long userLog_id) {
        Users users = this.userRepository.findById(userLog_id)
                .orElseThrow(() -> new NotFoundException("Id not found!"));

        // Localização
        Location location = locationRepository.findById(Long.valueOf(data.id_location()))
                .orElseThrow(() -> new RuntimeException("Id location Não encontrado!"));

        // Proprietário principal
        MainOwner mainOwner = mainOwnerRepository.findById(String.valueOf(data.id_owner()))
                .orElseThrow(() -> new RuntimeException("Id main owner Não encontrado"));

        if (!mainOwner.isOperative()) {
            throw new OperativeFalseException("The inactive mainowner cannot be accessed.");
        }

        System.out.println(data.name_equipment());


        // Cria o equipamento


        Equipment equipment  = equipmentRepository.save(new Equipment(data,getStartDateFromQuarter(data.validity()), location, mainOwner));


        var userLog = new UserLog(users, "Equipment", data.id_equipment(), "Create new Equipment", EnumAction.CREATE);
        userLogRepository.save(userLog);

        // Salvar o tracking history
        Enviroment enviroment = location.getEnvironment();

        TrackingHistory trackingHistory = new TrackingHistory(
                equipment, enviroment, equipment.getRfid(), EnumTrackingAction.ENTER,
                EnumColors.GREEN
        );

        trackingHistoryRepository.save(trackingHistory);

        System.out.println(new EquipmentDetailsDTO(equipment));
        System.out.println("Post: Equipment ");
        return new EquipmentDetailsDTO(equipment);
    }


    @Override
    public EquipmentDetailsDTO updateEquipment(String id_equipment, EquipmentUpdateDTO data, Long userLog_id) {
        var equipment = equipmentRepository.findById(id_equipment)
                .orElseThrow(() -> new NotFoundException("Id not found"));

        if(!equipment.isOperative()){
            throw new OperativeFalseException("The inactive equipment cannot be updated.");
        }

        Users user = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var userlog = new UserLog(user,"Equipment",data.id_equipment(),null,"Infos update",EnumAction.UPDATE);

        if (data.id_equipment() != null){
            if(data.id_equipment().isBlank()){
                throw new BlankErrorException("Equipment id cannot be blank");
            }
            equipment.setIdEquipment(data.id_equipment());
            userlog.setField("equipment id to: " + data.id_equipment());
        }
        if (data.name_equipment() != null){
            if(data.name_equipment().isBlank()){
                throw new BlankErrorException("Equipment name cannot be blank");
            }
            equipment.setName_equipment(data.name_equipment());
            userlog.setField(userlog.getField()+" ,"+"equipment name to: " + data.name_equipment());
        }
        if (data.rfid() != 0) {
            equipment.setRfid(data.rfid());
            userlog.setField(userlog.getField()+" ,"+"equipment rfid to: " + data.rfid());
        }

        if (data.type() != null) {
            if(data.type().isBlank()){
                throw new BlankErrorException("Equipment type cannot be blank");
            }
            equipment.setType(data.type());
            userlog.setField(userlog.getField()+" ,"+"equipment type to: " + data.type());
        }

        if (data.model() != null) {
            equipment.setModel(data.model());
            userlog.setField(userlog.getField()+" ,"+"equipment model to: " + data.model());
        }

        if (data.validity() != null) {
            if(data.validity().isBlank()){
                throw new BlankErrorException("Equipment validity cannot be blank");
            }
            equipment.setValidity(getStartDateFromQuarter(data.validity()));
            userlog.setField(userlog.getField()+" ,"+"equipment validity to: " + data.validity());
        }

        if (data.admin_rights() != null) {
            if(data.admin_rights().isBlank()){
                throw new BlankErrorException("Admin rights cannot be blank");
            }
            equipment.setAdmin_rights(data.admin_rights());
            userlog.setField(userlog.getField()+" ,"+"equipment admin rights to: " + data.admin_rights());
        }

        if (data.observation() != null) {
            if(data.observation().isBlank()){
                throw new BlankErrorException("Observation cannot be blank");
            }
            equipment.setObservation(data.observation());
            userlog.setField(userlog.getField()+" ,"+"equipment observation to: " + data.observation());
        }

        if (data.id_location() != null) {
            var location = locationRepository.findById(data.id_location())
                    .orElseThrow(() -> new NotFoundException("Location id not found"));
            equipment.setLocation(location);
            userlog.setField(userlog.getField()+" ,"+"equipment location to: " + data.id_location());
        }

        if (data.id_owner() != null) {
            if(data.id_owner().isBlank()){
                throw new BlankErrorException("Owner id cannot be blank");
            }
            var owner = mainOwnerRepository.findById(data.id_owner())
                    .orElseThrow(() -> new NotFoundException("Owner id not found"));
            equipment.setOwner(owner);
            userlog.setField(userlog.getField()+" ,"+"equipment main owner to: " + data.id_owner());
        }

        userLogRepository.save(userlog);

        // Salva a entidade atualizada no repositório
        equipmentRepository.save(equipment);
        return new EquipmentDetailsDTO(equipment);
    }

    @Override
    public void activateEquipment(String id_equipment, Long userLog_id) {
        Users users = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        var equipment = equipmentRepository.findById(id_equipment).orElseThrow(() -> new NotFoundException("Id not found"));

        if (equipment.isOperative()){
            throw new OpetativeTrueException("It is already activate");
        }

        equipment.setOperative(true);
        var userLog = new UserLog(users, "Equipment", id_equipment, "Operative", "Activated area", EnumAction.UPDATE);
        equipmentRepository.save(equipment);
        userLogRepository.save(userLog);
    }

    @Override
    public void inactivateEquipment(String id_equipment, Long userLog_id) {

        var equipment = equipmentRepository.findById(id_equipment).orElseThrow(() -> new NotFoundException("Id not found"));

        if (!equipment.isOperative()){
            throw new OperativeFalseException("It is already inactivate");
        }
        Users users = this.userRepository.findById(userLog_id).orElseThrow(() -> new NotFoundException("Id not found"));
        equipment.setOperative(false);
        var userLog = new UserLog(users, "Equipment", id_equipment, "Operative", "Inactivated area", EnumAction.UPDATE);
        equipmentRepository.save(equipment);
        userLogRepository.save(userLog);
    }

    @Override
    public List<EquipmentDetailsDTO> getEquipmentValidation(int page, int itens, String validity,
                                                            String environment, String mainOwner,
                                                            String id_owner, String id_equipment,
                                                            String name_equipment, String post) {

        LocalDate validDate = getStartDateFromQuarter(validity);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Equipment> criteriaQuery = criteriaBuilder.createQuery(Equipment.class);

        //Select From Equipment
        Root<Equipment> equipmentRoot = criteriaQuery.from(Equipment.class);

        //Inner Join
        Join<Equipment,MainOwner> mainOwnerJoin = equipmentRoot.join("owner");
        Join<Equipment, Location> locationJoin = equipmentRoot.join("location");
        Join<Equipment, Location> locationPostJoin = equipmentRoot.join("location");
        Join<Location, Post> PostJoin = locationPostJoin.join("post");
        Join<Location, Enviroment> enviromentJoin = locationJoin.join("environment");


        List<Predicate> predicate = new ArrayList<>();

        //WHERE

        if(validity != null){
            predicate.add(criteriaBuilder.like(equipmentRoot.get("validity"), "%"+validDate+"%"));
        }
        if (environment != null){
            predicate.add(criteriaBuilder.like(enviromentJoin.get("environment_name"), "%"+environment+"%"));
        }
        if (mainOwner != null){
            predicate.add(criteriaBuilder.like(mainOwnerJoin.get("owner_name"), "%" + mainOwner + "%"));
        }
        if(id_owner != null){
            predicate.add(criteriaBuilder.like(equipmentRoot.get("id_owner"), "%" + id_owner + "%"));
        }
        if (id_equipment != null){
            predicate.add(criteriaBuilder.like(equipmentRoot.get("id_equipment"), "%" + id_equipment + "%"));
        }
        if (name_equipment != null){
            predicate.add(criteriaBuilder.like(equipmentRoot.get("name_equipment"), "%" + name_equipment + "%"));
        }
        if (post != null){
            predicate.add(criteriaBuilder.like(PostJoin.get("post"), "%" + post + "%"));
        }

        if (validity != null && environment != null && mainOwner != null && id_owner != null && id_equipment != null && name_equipment != null && post != null){
            return equipmentRepository.findAllByOperativeTrue(PageRequest.of(page, itens))
                    .stream()
                    .map(EquipmentDetailsDTO::new)
                    .collect(Collectors.toList());
        }

        predicate.add(criteriaBuilder.equal(equipmentRoot.get("operative"), true));

        criteriaQuery.where(criteriaBuilder.and(predicate.toArray(new Predicate[0])));
        
        TypedQuery<Equipment> query = entityManager.createQuery((criteriaQuery));


        return query.getResultList().stream()
                .map(EquipmentDetailsDTO::new)
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> uploadImageEquipament (MultipartFile file,EnumModelEquipment equipType){
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            file.transferTo(targetLocation);

            equipament_image(targetLocation,equipType);

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

        Image image  = imageRepository.findByModel(equipmentModel);
        if(image==null){

            Image newimage = imageRepository.save(new Image(targetLocatioString,equipmentModel));
            for (Equipment equipment : allEquipments) {
                equipment.setId_image(newimage);
            }
        }

        image.setImage(targetLocatioString);
        imageRepository.save(image);



        equipmentRepository.saveAll(allEquipments);
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

}

