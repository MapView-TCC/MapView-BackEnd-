package com.MapView.BackEnd.Service;


import com.MapView.BackEnd.dtos.CostCenter.CostCenterCreateDTO;
import com.MapView.BackEnd.dtos.CostCenter.CostCenterDetailsDTO;

public interface CostCenterService {

    void getCostCenter(Long id_cost_center);
    void getAllCostCenter();
    CostCenterDetailsDTO createCostCenter(CostCenterCreateDTO dados);
    void updateCostCenter(String cost_center_name);
    void activateCostCenter(Long id_cost_center); // put
    void inactivateCostCenter(Long id_cost_center); // put
}
