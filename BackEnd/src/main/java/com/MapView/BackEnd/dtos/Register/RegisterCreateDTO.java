package com.MapView.BackEnd.dtos.Register;

import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleCrateDTO;
import com.MapView.BackEnd.enums.EnumCourse;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record RegisterCreateDTO (
        //Equipment

        String id_equipment,

        String name_equipment,
        Long rfid,

        String type,
        EnumModelEquipment model,


        String validity,
        String admin_rights,
        String observation,
        //location Equipment


        Long id_building,


        Long id_eviroment,

        String post,


        String id_owner,

        String costCenter_name,
        EnumCourse enumCourse,
        String name_classes,
        LocalDate criation_date,
        List<ResponsibleResgisterDTO> dataResposible

) {
}
