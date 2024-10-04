package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.infra.OperativeFalseException;
import com.MapView.BackEnd.infra.OpetativeTrueException;
import com.MapView.BackEnd.repository.EquipmentRepository;
import com.MapView.BackEnd.repository.EquipmentResponsibleRepository;
import com.MapView.BackEnd.repository.ResponsibleRepository;
import com.MapView.BackEnd.service.EquipmentResponsibleService;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleUpdateDTO;
import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.EquipmentResponsible;
import com.MapView.BackEnd.entities.Responsible;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentResponsibleServiceImp implements EquipmentResponsibleService {


    private final EquipmentResponsibleRepository equipmentResponsibleRepository;


    private final EquipmentRepository equipmentRepository;


    private  final  ResponsibleRepository responsibleRepository;

    public EquipmentResponsibleServiceImp(EquipmentResponsibleRepository equipmentResponsibleRepository, EquipmentRepository equipmentRepository, ResponsibleRepository responsibleRepository) {
        this.equipmentResponsibleRepository = equipmentResponsibleRepository;
        this.equipmentRepository = equipmentRepository;
        this.responsibleRepository = responsibleRepository;
    }

    @Override
    public EquipmentResponsibleDetailsDTO getEquipmentResponsible(Long id_equip_resp) {
        EquipmentResponsible equipmentResponsible = this.equipmentResponsibleRepository.findById(id_equip_resp)
                .orElseThrow(() -> new NotFoundException("Equipment responsible id not found"));

        return new EquipmentResponsibleDetailsDTO(equipmentResponsible);
    }

    @Override
    public List<EquipmentResponsibleDetailsDTO> getAllEquipmentResponsible() {
        return equipmentResponsibleRepository.findAllByOperativeTrue().stream().map(EquipmentResponsibleDetailsDTO::new).toList();
    }

    @Override
    public EquipmentResponsibleDetailsDTO createEquipmentResponsible(EquipmentResponsibleCreateDTO data) {
        Equipment equipment = equipmentRepository.findById(data.id_equipment())
                .orElseThrow(() -> new NotFoundException("Equipment id not found"));

        Responsible responsible = responsibleRepository.findById(data.id_responsible())
                .orElseThrow(() -> new NotFoundException("Responsible id not found"));

        EquipmentResponsible equipmentResponsible = new EquipmentResponsible(data, equipment, responsible);

        equipmentResponsibleRepository.save(equipmentResponsible);
        System.out.println("Post: Equipmentresponsible ");

        return new EquipmentResponsibleDetailsDTO(equipmentResponsible);

    }

    @Override
    public EquipmentResponsibleDetailsDTO updateEquipmentResponsible(Long id_equip_resp, EquipmentResponsibleUpdateDTO dados) {

        var equipmentResponsible = equipmentResponsibleRepository.findById(id_equip_resp)
                .orElseThrow(() -> new NotFoundException("Equipment responsible id not found"));

        if (dados.id_equipment() != null){
            var equipment = equipmentRepository.findById(dados.id_equipment())
                    .orElseThrow(() -> new NotFoundException("Equipment id not found"));
            if (!equipment.isOperative()){
                throw new OperativeFalseException("The inactive equipment cannot be accessed.");
            }
            equipmentResponsible.setIdEquipment(equipment);
        }

        if (dados.id_responsible() != null){
            var responsible = responsibleRepository.findById(dados.id_responsible())
                    .orElseThrow(() -> new NotFoundException("Responsible id not found"));
            if (!responsible.isOperative()){
                throw new OperativeFalseException("The inactive responsible cannot be accessed.");
            }
            equipmentResponsible.setId_responsible(responsible);
        }

        if (dados.start_usage() != null){
            equipmentResponsible.setStart_usage(dados.start_usage());
        }

        if (dados.end_usage() != null) {
            // ver se a data inicial não está vazia
            if (dados.start_usage() == null) {
                throw new IllegalArgumentException("Start date must be set before setting the end date.");
            }
            // O método isBefore é usado para comparar instantes de tempo, datas e horas.
            if (dados.end_usage().isBefore(dados.start_usage())) {
                throw new IllegalArgumentException("The end date must be greater than or equal to the start date.");
            }

            equipmentResponsible.setEnd_usage(dados.end_usage());
        }

        equipmentResponsibleRepository.save(equipmentResponsible);
        return new EquipmentResponsibleDetailsDTO(equipmentResponsible);
    }


    @Override
    public void activateEquipmentResponsible(Long id_equip_resp) {
        var equipmentResponsible = equipmentResponsibleRepository.findById(id_equip_resp) .orElseThrow(() -> new NotFoundException("Equipment id not found"));
        if (equipmentResponsible.isOperative()){
            throw new OpetativeTrueException("It is already activate");
        }
        equipmentResponsible.setOperative(true);
        throw new OperativeFalseException("");
    }

    @Override
    public void inactivateEquipmentResponsible(Long id_equip_resp) {
        var equipmentResponsible = equipmentResponsibleRepository.findById(id_equip_resp).orElseThrow(() -> new NotFoundException("Equipment id not found"));
        if (!equipmentResponsible.isOperative()){
            throw new OperativeFalseException("It is already inactivate");
        }
        equipmentResponsible.setOperative(false);
        throw new OperativeFalseException("");
    }
}
