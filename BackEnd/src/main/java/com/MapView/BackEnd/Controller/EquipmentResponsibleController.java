package com.MapView.BackEnd.Controller;

import com.MapView.BackEnd.Repository.EquipmentResponsibleRepository;
import com.MapView.BackEnd.ServiceImp.EquipmentResponsibleServiceImp;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipmentresponsible")
public class EquipmentResponsibleController {

    @Autowired
    private EquipmentResponsibleServiceImp equipmentResponsibleServiceImp;

    @GetMapping
    public ResponseEntity<List<EquipmentResponsibleDetailsDTO>> getAllEquipmentResponsible(){
        var list = equipmentResponsibleServiceImp.getAllEquipmentResponsible();
        return ResponseEntity.ok(list);
    }
}
