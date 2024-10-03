package com.MapView.BackEnd.dtos.Register;

import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleCrateDTO;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterCreateDTO (
        //Equipment
        @NotBlank(message = "Equipment id cannot be blank")
        @Size(min = 8,message = "Equipment_id must be size 8")
        String id_equipment,
        @NotBlank(message = "Equipment name cannot be blank")
        String name_equipment,
        Long rfid,
        @NotBlank(message = "Type cannot be blank")
        String type,
        EnumModelEquipment model,
        String validity,
        String admin_rights,
        String observation,
        //location Equipment
        @Positive
        @Min(1)
        Long id_building,
        @NotBlank
        String post,
        @Positive
        @Min(1)
        Long id_eviroment,

        @NotBlank(message = "Owner id cannot be blank.")
        String id_owner,
        @NotBlank(message = "Owner name cannot be blank.")
        String owner_name,

        @NotBlank(message = "CostCenter name cannot be blank")
        String costCenter_name,
        String name_classes,
        List<ResponsibleCrateDTO> dataResposible

) {
}
