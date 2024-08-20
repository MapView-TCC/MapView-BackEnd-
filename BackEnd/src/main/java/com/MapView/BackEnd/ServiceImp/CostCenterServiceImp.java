package com.MapView.BackEnd.ServiceImp;

import com.MapView.BackEnd.dtos.CostCenter.CostCenterCreateDTO;
import com.MapView.BackEnd.Repository.CostCenterRepository;
import com.MapView.BackEnd.Service.CostCenterService;
import com.MapView.BackEnd.dtos.CostCenter.CostCenterDetailsDTO;
import com.MapView.BackEnd.entities.CostCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CostCenterServiceImp implements CostCenterService {

    @Autowired
    private CostCenterRepository costCenterRepository;

    @Override
    public void getCostCenter(Long id_cost_center) {

    }

    @Override
    public void getAllCostCenter() {

    }

    @Override
    public CostCenterDetailsDTO createCostCenter(CostCenterCreateDTO dados) {
        var costcenter = new CostCenter(dados);
        costCenterRepository.save(costcenter);
        return new CostCenterDetailsDTO(costcenter);
    }

    @Override
    public void updateCostCenter(String cost_center_name) {

    }

    @Override
    public void activateCostCenter(Long id_cost_center) {

    }

    @Override
    public void inactivateCostCenter(Long id_cost_center) {

    }
}
