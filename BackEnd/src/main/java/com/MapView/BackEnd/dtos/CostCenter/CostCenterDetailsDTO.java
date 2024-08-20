package com.MapView.BackEnd.dtos.CostCenter;

import com.MapView.BackEnd.entities.CostCenter;

public record CostCenterDetailsDTO(Long id_cost_center, String cost_center_name, boolean operative) {
    public CostCenterDetailsDTO (CostCenter costCenter){
        this(costCenter.getId_cost_center(), costCenter.getCost_center_name(), costCenter.isOperative());
    }
}
