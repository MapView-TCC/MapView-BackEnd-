package com.MapView.BackEnd.Service;

import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleUpdateDTO;
import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.Location;
import com.MapView.BackEnd.entities.MainOwner;
import com.MapView.BackEnd.entities.Responsible;

import java.time.LocalDate;
import java.util.List;

public interface EquipmentResponsibleService {

    EquipmentResponsibleDetailsDTO getEquipmentResponsible(Long id_equip_resp);
    List<EquipmentResponsibleDetailsDTO> getAllEquipmentResponsible();
    EquipmentResponsibleDetailsDTO createEquipmentResponsible(EquipmentResponsibleCreateDTO equipmentResponsibleCreateDTO);
    EquipmentResponsibleDetailsDTO updateEquipmentResponsible(Long id_equip_resp, EquipmentResponsibleUpdateDTO dados);

    void activateEquipmentResponsible(Long id_equip_resp); // put
    void inactivateEquipmentResponsible(Long id_equip_resp); // put

}
