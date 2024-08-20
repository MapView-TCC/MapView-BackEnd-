package com.MapView.BackEnd.Controller;

import com.MapView.BackEnd.dtos.CostCenter.CostCenterCreateDTO;
import com.MapView.BackEnd.ServiceImp.CostCenterServiceImp;
import com.MapView.BackEnd.dtos.CostCenter.CostCenterDetailsDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/costcenter")
public class CostCenterController {

    @Autowired
    private CostCenterServiceImp costCenterServiceImp;

    @PostMapping
    @Transactional
    public ResponseEntity<CostCenterDetailsDTO> cadastro(@RequestBody @Valid CostCenterCreateDTO dados, UriComponentsBuilder uriBuilder){
        var costcenter = costCenterServiceImp.createCostCenter(dados);

        // boa pratica, para retornar o caminho
        var uri = uriBuilder.path("/api/v1/costcenter/{id}").buildAndExpand(costcenter.id_cost_center()).toUri();
        return ResponseEntity.created(uri).body(new CostCenterDetailsDTO(costcenter.id_cost_center(), costcenter.cost_center_name(), costcenter.operative()));
    }
}
