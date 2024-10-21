package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.dtos.TrackingHistory.TrackingHistoryWrongLocationDTO;
import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumColors;
import com.MapView.BackEnd.enums.EnumTrackingAction;
import com.MapView.BackEnd.repository.*;
import com.MapView.BackEnd.service.TrackingHistoryService;
import com.MapView.BackEnd.dtos.TrackingHistory.TrackingHistoryCreateDTO;
import com.MapView.BackEnd.dtos.TrackingHistory.TrackingHistoryDetailsDTO;
import com.MapView.BackEnd.infra.Exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrackingHistoryServiceImp implements TrackingHistoryService {


    private final TrackingHistoryRepository trackingHistoryRepository;
    private final EnvironmentRepository environmentRepository;
    private final EquipmentRepository equipmentRepository;
    private final LocationRepository locationRepository;
    private final EquipmentResponsibleRepository equipmentResponsibleRepository;

    private  SimpMessagingTemplate template;


    public TrackingHistoryServiceImp(TrackingHistoryRepository trackingHistoryRepository, EnvironmentRepository environmentRepository, EquipmentRepository equipmentRepository, LocationRepository locationRepository, EquipmentResponsibleRepository equipmentResponsibleRepository, SimpMessagingTemplate template) {
        this.trackingHistoryRepository = trackingHistoryRepository;
        this.environmentRepository = environmentRepository;
        this.equipmentRepository = equipmentRepository;
        this.locationRepository = locationRepository;
        this.equipmentResponsibleRepository = equipmentResponsibleRepository;
        this.template = template;
    }

    @Override
    public TrackingHistoryDetailsDTO getTrackingHistory(Long id_tracking) {
        TrackingHistory trackingHistory = this.trackingHistoryRepository.findById(id_tracking)
                .orElseThrow(() -> new NotFoundException("Id not found"));

        return new TrackingHistoryDetailsDTO(trackingHistory);
    }

    @Override
    public List<TrackingHistoryDetailsDTO> getAllTrackingHistory() {
        return trackingHistoryRepository.findAll().stream().map(TrackingHistoryDetailsDTO::new).toList();


    }

    @Async // Indica que o método pode ser executado de forma assíncrona.
    @Override
    @CrossOrigin("http://localhost:3001") // Permite que requisições de um domínio específico (localhost:3001) acessem este endpoint.
    public TrackingHistoryDetailsDTO createTrackingHistory(TrackingHistoryCreateDTO dados) {
        // Busca um ambiente pelo ID fornecido nos dados. Se não for encontrado, lança uma exceção NotFoundException.
        Environment local_tracking = environmentRepository.findById(dados.id_environment())
                .orElseThrow(() -> new NotFoundException("Id not found"));

        // Tenta encontrar um equipamento pelo RFID fornecido nos dados.
        Optional<Equipment> equipment = equipmentRepository.findByRfid(dados.rfid());

        TrackingHistory history = new TrackingHistory();

        // isEmpty -> Retorna um valor booliano que indica se um variável foi inicializado.
        if (equipment.isEmpty()) {
            // Salva um novo histórico de rastreamento com o RFID e ambiente encontrado, marcando como 'RED'.

            // Cria um novo equipamento com um UUID aleatório e o RFID fornecido.
            Equipment emptyEquipment = equipmentRepository.save(new Equipment(UUID.randomUUID().toString().substring(0,8), dados.rfid()));
            TrackingHistory trackingHistory = trackingHistoryRepository.save(new TrackingHistory(emptyEquipment, local_tracking,EnumTrackingAction.ENTER, EnumColors.RED));

            trackingHistoryRepository.save(trackingHistory);  // Salva o histórico de rastreamento.

            // Envia a atualização para o tópico "/equip". - AQUI É O WEB SOCKET?
            template.convertAndSend("/equip",trackingHistory);

            return new TrackingHistoryDetailsDTO(trackingHistory);
        }

        // Busca o último histórico de rastreamento para o equipamento encontrado, ordenando por data e hora em ordem decrescente.
        TrackingHistory last_track = trackingHistoryRepository.findTop1ByEquipmentOrderByIdDesc(equipment.get());

        // Se não houver histórico anterior, lança uma exceção.
        if (last_track == null){
            throw new NotFoundException("RFID tag " + dados.rfid() + " is not linked to any equipment ");
        }

        // Obtém o ambiente do último rastreamento.
        Environment last_track_local = last_track.getEnvironment();

        // Verifica se o ambiente do último rastreamento é igual ao ambiente atual.
        if (last_track_local.equals(local_tracking)) {

            // Verifica se a última ação foi 'OUT'.
            if (last_track.getAction().equals(EnumTrackingAction.OUT)){

                // Se o ambiente atual é "BTC".
                if (local_tracking.getEnvironment_name().equals("BTC")){

                    System.out.println("_-----1--------");
                    // Salva novo histórico de rastreamento marcando a ação como 'ENTER' e cor como 'GREEN'.
                    TrackingHistory trackingHistory = trackingHistoryRepository.save(new TrackingHistory(local_tracking, equipment.get(),EnumTrackingAction.ENTER, EnumColors.GREEN));
                    template.convertAndSend("/equip",trackingHistory);
                    return new TrackingHistoryDetailsDTO(trackingHistory);
                }
                System.out.println("_-----2--------");
                // Salva novo histórico de rastreamento marcando a ação como 'OUT' e cor como 'GREEN'.
                TrackingHistory trackingHistory = trackingHistoryRepository.save(new TrackingHistory(local_tracking, equipment.get(),EnumTrackingAction.ENTER, EnumColors.GREEN));
                template.convertAndSend("/equip",trackingHistory);
                return new TrackingHistoryDetailsDTO(trackingHistory);
            }

            // Se o ambiente atual é "BTC".
            if (local_tracking.getEnvironment_name().equals("BTC")){
                System.out.println("_-----3--------");
                // Salva novo histórico de rastreamento marcando a ação como 'OUT' e cor como 'YELLOW'.
                TrackingHistory trackingHistory = trackingHistoryRepository.save(new TrackingHistory(local_tracking, equipment.get(),EnumTrackingAction.OUT, EnumColors.YELLOW));
                template.convertAndSend("/equip",trackingHistory);
                return new TrackingHistoryDetailsDTO(trackingHistory);
            }
            // Salva novo histórico de rastreamento marcando a ação como 'OUT' e cor como 'GREEN'.
            TrackingHistory trackingHistory = trackingHistoryRepository.save(new TrackingHistory(local_tracking, equipment.get(),EnumTrackingAction.OUT, EnumColors.GREEN));
            template.convertAndSend("/equip",trackingHistory);
            System.out.println("_-----4--------");
            return new TrackingHistoryDetailsDTO(trackingHistory);
        }
        System.out.println("_-----5--------");
        // Salva histórico de rastreamento com o ambiente do último rastreamento e ação 'OUT'.
        trackingHistoryRepository.save(new TrackingHistory(last_track_local,equipment.get(),  EnumTrackingAction.OUT,EnumColors.GREEN));

        // Salva novo histórico de rastreamento com o ambiente atual e ação 'ENTER'.
        TrackingHistory trackingHistory = trackingHistoryRepository.save(new TrackingHistory(local_tracking, equipment.get(),  EnumTrackingAction.ENTER, EnumColors.GREEN));
        template.convertAndSend("/equip",trackingHistory);


        // Retorna os detalhes do histórico de rastreamento.
        return new TrackingHistoryDetailsDTO(trackingHistory);
    }




    @Override
    public List<TrackingHistoryDetailsDTO> FilterTracking(int page, int itens, EnumTrackingAction action, Integer day,
                                                          Integer month, Integer year, EnumColors colors, String id_equipment) {

        List<TrackingHistory> filterTracking;

        // Se a ação não for nula, filtra pelo valor da ação
        filterTracking = trackingHistoryRepository.findAll(PageRequest.of(page, itens)).stream()
                .filter(t -> (action == null || t.getAction() == action))
                .filter(t -> (colors == null || t.getWarning() == colors))
                .filter(t -> (id_equipment == null || (t.getEquipment() != null && t.getEquipment().getCodigo().equalsIgnoreCase(id_equipment))))
                .filter(t -> {
                    // Converte o Instant para LocalDateTime no fuso horário local
                    LocalDateTime dateTime = t.getDatetime().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    boolean match = true; // é true pois está assumindo que o registro deve ser incluído no resultado, a menos que uma das condições subsequentes o desqualifique.
                    // Se day, month, e year forem null: match permanece true e o registro passa no filtro.
                    // Se day for 30 e o dia em dateTime também for 30: match continua true.
                    // Se month for 8, mas o mês em dateTime for 7: match se torna false e o registro é filtrado fora.
                    if (day != null) {
                        match = match && dateTime.getDayOfMonth() == day;
                    }
                    if (month != null) {
                        match = match && dateTime.getMonthValue() == month;
                    }
                    if (year != null) {
                        match = match && dateTime.getYear() == year;
                    }
                    return match;
                })
                .toList();

        // Converte para DTO
        return filterTracking.stream()
                .map(TrackingHistoryDetailsDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTracking(Long id_tracking) {
        var tracking = trackingHistoryRepository.findById(id_tracking).orElseThrow(() ->
                new NotFoundException("Id not found"));
        if (tracking != null){
            trackingHistoryRepository.deleteById(id_tracking);
        }
    }

    public List<TrackingHistoryWrongLocationDTO> findWrongLocationEquipments(Long id_environment) {
        // Verifica se o ambiente existe
        Environment environment = environmentRepository.findById(id_environment)
                .orElseThrow(() -> new NotFoundException("Environment not found"));

        List<TrackingHistory> trackingHistory = trackingHistoryRepository.findByEnvironment(environment);
        List<TrackingHistoryWrongLocationDTO> wrongLocationDTOs = new ArrayList<>();

        Set<String> addedEquipmentIds = new HashSet<>();  // Usar um Set para evitar duplicações

        for (TrackingHistory track : trackingHistory) {
            if (track.getAction().equals(EnumTrackingAction.ENTER)) {
                Equipment equipment = track.getEquipment();
                Long locationEquip = equipment.getLocation().getEnvironment().getId_environment();

                if (!locationEquip.equals(id_environment)) {
                    if (!addedEquipmentIds.contains(equipment.getCodigo())) {
                        addedEquipmentIds.add(equipment.getCodigo());

                        List<EquipmentResponsible> equipmentResponsibles = equipmentResponsibleRepository.findByIdEquipment(equipment);
                        List<String> responsibles = new ArrayList<>();

                        for (EquipmentResponsible responsible : equipmentResponsibles) {
                            responsibles.add(responsible.getId_responsible().getResponsible());
                        }

                        wrongLocationDTOs.add(new TrackingHistoryWrongLocationDTO(equipment, environment,responsibles));
                    }
                }
            }
        }

        return wrongLocationDTOs;
    }

    @Override
    public void deleteTrackingById(Long id_tracking) {
        var tracking  = trackingHistoryRepository.findById(id_tracking).orElseThrow(() ->
                new NotFoundException("Id not found"));
        if (tracking != null){
            trackingHistoryRepository.deleteById(id_tracking);
        }
    }

}



