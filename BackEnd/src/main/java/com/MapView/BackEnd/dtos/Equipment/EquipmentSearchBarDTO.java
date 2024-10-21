package com.MapView.BackEnd.dtos.Equipment;

import com.MapView.BackEnd.entities.*;
import com.MapView.BackEnd.enums.EnumModelEquipment;

import java.time.LocalDate;
import java.util.List;

public record EquipmentSearchBarDTO(String id_equipment,
                                     String name_equipment,
                                    String location,
                                    String owner,
                                    String environment,
                                    String currentEnvironment,
                                    List<String> responsible



                                    ) {

    public EquipmentSearchBarDTO(Equipment equipment,TrackingHistory location,List<String> responsibles){
        this(equipment.getCodigo(),
                equipment.getName_equipment(),
                equipment.getLocation().getPost().getPost(),
                equipment.getOwner().getId_owner(),
                equipment.getLocation().getEnvironment().getEnvironment_name(),
                location.getEnvironment().getEnvironment_name()

                ,responsibles);
    }


}
