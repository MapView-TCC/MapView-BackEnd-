package com.MapView.BackEnd.dtos.Register;

import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record RegisterDetailsDTO (String id_equipment,
                                  String name_equipment,
                                  long rfid,
                                  String type,
                                  EnumModelEquipment model,
                                  String validity,
                                  String admin_rights,
                                  String observation,
                                  Location location,
                                  MainOwner owner,
                                  Long id_location,
                                  Post post,
                                  Enviroment enviroment,
                                  Long id_equip_resp,
                                  Equipment equipment,
                                  Responsible responsible,
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
                                  LocalDate start_usage,
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
                                  LocalDate end_usage) {

    public RegisterDetailsDTO(Equipment equipment, Location location, EquipmentResponsible equipmentResponsible){
        this(equipment.getId_equipment(),
                equipment.getName_equipment(),
                equipment.getRfid(),
                equipment.getType(),
                equipment.getModel(),
                equipment.getValidity(),
                equipment.getAdmin_rights(),
                equipment.getObservation(),
                equipment.getLocation(),
                equipment.getOwner(),
                location.getId_location(),
                location.getPost(),
                location.getEnvironment(),
                equipmentResponsible.getId_equip_resp(),
                equipmentResponsible.getId_equipment(),
                equipmentResponsible.getId_responsible(),
                equipmentResponsible.getStart_usage(),
                equipmentResponsible.getEnd_usage());

    }

}
