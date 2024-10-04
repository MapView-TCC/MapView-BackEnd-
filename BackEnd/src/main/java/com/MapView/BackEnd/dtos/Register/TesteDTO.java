package com.MapView.BackEnd.dtos.Register;

import com.MapView.BackEnd.enums.EnumCourse;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record TesteDTO (
        @NotBlank(message = "Equipment id cannot be blank")
        @Size(min = 8,message = "Equipment_id must be size 8")
        String id_equipment
) {
}

