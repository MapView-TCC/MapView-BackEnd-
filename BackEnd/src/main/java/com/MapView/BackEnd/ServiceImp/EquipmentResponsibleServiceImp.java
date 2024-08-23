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
        var equipmentResponsible = equipmentRepository.findById(id_equip_resp)
                .orElseThrow(() -> new NotFoundException("Equipment responsible id not found"));


    }

    @Override
    public void activateEquipmentResponsible(Long id_equip_resp) {

    }

    @Override
    public void inactivateEquipmentResponsible(Long id_equip_resp) {

    }
}
