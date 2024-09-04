package com.MapView.BackEnd.dtos.Equipment;

import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.Location;
import com.MapView.BackEnd.entities.MainOwner;
import com.MapView.BackEnd.enums.EnumModelEquipment;

public record EquipmentDetailsDTO(
        String id_equipment,
        String name_equipment,
        String rfid,
        String type,
        EnumModelEquipment model,
        String validity,
        String admin_rights,
        String observation,
        Location id_location,
        MainOwner id_owner,
        boolean operative) {

    public EquipmentDetailsDTO(Equipment equipment){
        this(equipment.getId_equipment(), equipment.getName_equipment(), equipment.getRfid(), equipment.getType(), equipment.getModel(),
             equipment.getValidity(), equipment.getAdmin_rights(), equipment.getObservation(), equipment.getLocation(),
             equipment.getOwner(), equipment.isOperative());
    }

}
