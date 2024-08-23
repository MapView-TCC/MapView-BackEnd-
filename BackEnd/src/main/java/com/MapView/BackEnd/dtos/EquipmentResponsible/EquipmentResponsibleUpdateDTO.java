package com.MapView.BackEnd.dtos.EquipmentResponsible;

import java.time.LocalDate;

public record EquipmentResponsibleUpdateDTO (
        String id_equipment,
        Long id_responsible,
        LocalDate start_usage,
        LocalDate end_usage

) {
}
