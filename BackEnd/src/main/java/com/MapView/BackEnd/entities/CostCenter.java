package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.CostCenter.CostCenterCreateDTO;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "cost_center")
@Entity(name = "cost_center")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id_cost_center")

public class CostCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cost_center;

    private String cost_center_name;

    private boolean operative;

    public CostCenter(CostCenterCreateDTO dados) {
        this.cost_center_name = dados.cost_center_name();
        this.operative = true;
    }
}

