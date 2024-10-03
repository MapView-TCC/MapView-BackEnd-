package com.MapView.BackEnd.dtos.Equipment;

import com.MapView.BackEnd.enums.EnumModelEquipment;
import jakarta.validation.constraints.*;

public record  EquipmentCreateDTO(
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
        @Positive
        @Min(1)
        Long id_location,
        @NotBlank
        String id_owner
) {
}
