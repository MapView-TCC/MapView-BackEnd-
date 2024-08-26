package com.MapView.BackEnd.Controller;

import com.MapView.BackEnd.Repository.EquipmentResponsibleRepository;
import com.MapView.BackEnd.ServiceImp.EquipmentResponsibleServiceImp;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleUpdateDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryDetailsDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponsibleDetailsDTO> getEquipmentResponsible(@PathVariable Long id){
        EquipmentResponsibleDetailsDTO equipment = equipmentResponsibleServiceImp.getEquipmentResponsible(id);
        return ResponseEntity.ok(equipment);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EquipmentResponsibleDetailsDTO> cadastroEquipmentResponsible(@RequestBody @Valid EquipmentResponsibleCreateDTO equipmentResponsibleCreateDTO, UriComponentsBuilder uriBuilder){
        var equipmentResponsible = equipmentResponsibleServiceImp.createEquipmentResponsible(equipmentResponsibleCreateDTO);

        // boa pratica, para retornar o caminho
        var uri = uriBuilder.path("/api/v1/equipmentresponsible/{id}").buildAndExpand(equipmentResponsible.id_equip_resp()).toUri();
        return ResponseEntity.created(uri).body(new EquipmentResponsibleDetailsDTO(equipmentResponsible.id_equip_resp(), equipmentResponsible.id_equipment(), equipmentResponsible.id_responsible(), equipmentResponsible.start_usage(), equipmentResponsible.end_usage(), equipmentResponsible.operative()));
    }

    @PutMapping("/{id_equip_resp}")
    @Transactional
    public ResponseEntity<EquipmentResponsibleDetailsDTO> updateEquipmentResponsible(@PathVariable Long id_equip_resp, @RequestBody EquipmentResponsibleUpdateDTO dados){
        EquipmentResponsibleDetailsDTO updateEquipmentResponsible = equipmentResponsibleServiceImp.updateEquipmentResponsible(id_equip_resp, dados);
        return ResponseEntity.ok(updateEquipmentResponsible);
    }

    @PutMapping("/inactivate/{id}")
    @Transactional
    public ResponseEntity<Void> inactivate(@PathVariable Long id){
        equipmentResponsibleServiceImp.inactivateEquipmentResponsible(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/active/{id}")
    @Transactional
    public ResponseEntity<Void> active(@PathVariable Long id){
        equipmentResponsibleServiceImp.activateEquipmentResponsible(id);
        return ResponseEntity.ok().build();
    }
}


