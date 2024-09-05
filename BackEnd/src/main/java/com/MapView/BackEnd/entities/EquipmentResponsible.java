package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;


@Table(name = "equipment_responsible", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_equipment", "id_responsible"})})
@Entity(name = "equipment_responsible")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id_equip_resp")
public class EquipmentResponsible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_equip_resp;

    @ManyToOne
    @JoinColumn(name = "id_equipment", nullable = false)
    @JsonBackReference
    private Equipment idEquipment;

    @ManyToOne
    @JoinColumn(name = "id_responsible", nullable = false)
    @JsonBackReference
    private Responsible id_responsible;

    private LocalDate start_usage;
    private LocalDate end_usage;

    private boolean operative;


    public EquipmentResponsible(EquipmentResponsibleCreateDTO equipmentResponsibleCreateDTO, Equipment equipment,
                                Responsible responsible) {
        this.idEquipment = equipment;
        this.id_responsible = responsible;
        this.start_usage = equipmentResponsibleCreateDTO.start_usage();
        this.end_usage = null;
        this.operative = true;
    }
}
