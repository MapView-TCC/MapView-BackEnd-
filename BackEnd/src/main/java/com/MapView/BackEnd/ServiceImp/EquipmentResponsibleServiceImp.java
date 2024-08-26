package com.MapView.BackEnd.ServiceImp;

import com.MapView.BackEnd.Repository.EquipmentRepository;
import com.MapView.BackEnd.Repository.EquipmentResponsibleRepository;
import com.MapView.BackEnd.Repository.ResponsibleRepository;
import com.MapView.BackEnd.Service.EquipmentResponsibleService;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleUpdateDTO;
import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.EquipmentResponsible;
import com.MapView.BackEnd.entities.Responsible;
import com.MapView.BackEnd.infra.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentResponsibleServiceImp implements EquipmentResponsibleService {

    @Autowired
    EquipmentResponsibleRepository equipmentResponsibleRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    ResponsibleRepository responsibleRepository;

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
    public EquipmentResponsibleDetailsDTO createEquipmentResponsible(EquipmentResponsibleCreateDTO equipmentResponsibleCreateDTO) {
        Equipment equipment = equipmentRepository.findById(equipmentResponsibleCreateDTO.id_equipment())
                .orElseThrow(() -> new NotFoundException("Equipment id not found"));

        Responsible responsible = responsibleRepository.findById(equipmentResponsibleCreateDTO.id_responsible())
                .orElseThrow(() -> new NotFoundException("Responsible id not found"));

        EquipmentResponsible equipmentResponsible = new EquipmentResponsible(equipmentResponsibleCreateDTO, equipment, responsible);

        equipmentResponsibleRepository.save(equipmentResponsible);

        return new EquipmentResponsibleDetailsDTO(equipmentResponsible);

    }

    @Override
    public EquipmentResponsibleDetailsDTO updateEquipmentResponsible(Long id_equip_resp, EquipmentResponsibleUpdateDTO dados) {
        var equipmentResponsible = equipmentResponsibleRepository.findById(id_equip_resp)
                .orElseThrow(() -> new NotFoundException("Equipment responsible id not found"));

        if (dados.id_equipment() != null){
            var equipment = equipmentRepository.findById(dados.id_equipment())
                    .orElseThrow(() -> new NotFoundException("Equipment id not found"));
            equipmentResponsible.setId_equipment(equipment);
        }

        if (dados.id_responsible() != null){
            var responsible = responsibleRepository.findById(dados.id_responsible())
                    .orElseThrow(() -> new NotFoundException("Responsible id not found"));
            equipmentResponsible.setId_responsible(responsible);
        }

        if (dados.start_usage() != null){
            equipmentResponsible.setStart_usage(dados.start_usage());
        }

        if (dados.end_usage() != null){
            equipmentResponsible.setEnd_usage(dados.end_usage());
        }

        equipmentResponsibleRepository.save(equipmentResponsible);
        return new EquipmentResponsibleDetailsDTO(equipmentResponsible);
    }

    @Override
    public void activateEquipmentResponsible(Long id_equip_resp) {
        var equipmentResponsibleClass = equipmentResponsibleRepository.findById(id_equip_resp);
        if (equipmentResponsibleClass.isPresent()){
            var equipmentResponsible = equipmentResponsibleClass.get();
            equipmentResponsible.setOperative(true);
        }
    }

    @Override
    public void inactivateEquipmentResponsible(Long id_equip_resp) {
        var equipmentResponsibleClass = equipmentResponsibleRepository.findById(id_equip_resp);
        if (equipmentResponsibleClass.isPresent()){
            var equipmentResponsible = equipmentResponsibleClass.get();
            equipmentResponsible.setOperative(false);
        }

    }
}
