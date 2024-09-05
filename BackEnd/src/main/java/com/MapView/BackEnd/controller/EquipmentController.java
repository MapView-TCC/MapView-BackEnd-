package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.dtos.Equipment.*;

import com.MapView.BackEnd.dtos.ImageUpload.UploadCreateDTO;
import com.MapView.BackEnd.serviceImp.EquipmentServiceImp;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipment")
public class EquipmentController {


    private final EquipmentServiceImp equipmentServiceImp;

    public EquipmentController(EquipmentServiceImp equipmentServiceImp) {
        this.equipmentServiceImp = equipmentServiceImp;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EquipmentDetailsDTO> createEquipament(@RequestBody @Valid EquipmentCreateDTO dados, UriComponentsBuilder uriBuilder, @RequestParam Long user_id){
        var equipment = equipmentServiceImp.createEquipment(dados, user_id);

        // boa pratica, para retornar o caminho
        var uri = uriBuilder.path("/api/v1/equipment/{id}").buildAndExpand(equipment.id_equipment()).toUri();
        return ResponseEntity.created(uri).body(new EquipmentDetailsDTO(equipment.id_equipment(), equipment.name_equipment(), equipment.rfid(), equipment.type(),
                equipment.model(), equipment.validity(), equipment.admin_rights(), equipment.observation(), equipment.id_location(),
                equipment.id_owner(), equipment.operative()));
    }

    @PostMapping("/image")
    @Transactional
    public ResponseEntity<String> uploadImage(@RequestBody UploadCreateDTO data){
        return equipmentServiceImp.uploadImageEquipament(data.file(),data.equipment());
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDetailsDTO>> getAllEquipment(@RequestParam(required = false) int page , @RequestParam(required = false) int itens, @RequestParam Long user_id){
        page=0;
        itens =10 ;
        var list = equipmentServiceImp.getAllEquipment(page,itens, user_id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EquipmentDetailsDTO>> getAllEquipmentFilter(@RequestParam(required = false)int page, @RequestParam(required = false)int itens, @RequestParam(required = false)  String validity,
                                                                             @RequestParam(required = false)  String enviroment, @RequestParam(required = false)  String mainowner,
                                                                           @RequestParam(required = false) String id_owner, @RequestParam(required = false) String id_equipment,
                                                                           @RequestParam(required = false) String name_equipment, @RequestParam(required = false) String post){

        var list = equipmentServiceImp.getEquipmentValidation(page,itens, validity,enviroment,mainowner, id_owner, id_equipment,
                                                             name_equipment, post);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{equipament_id}")
    public ResponseEntity<EquipmentDetailsDTO> getById(@PathVariable("equipament_id") String id, @RequestParam Long user_id){
        var equipment = equipmentServiceImp.getEquipment(id, user_id);
        return ResponseEntity.ok(equipment);
    }

    @PutMapping("/inactivate/{id}")
    @Transactional
    public ResponseEntity<Void> inactivate(@PathVariable String id, @RequestParam Long user_id){
        equipmentServiceImp.inactivateEquipment(id, user_id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/active/{id}")
    @Transactional
    public ResponseEntity<Void> active(@PathVariable String id, @RequestParam Long user_id){
        equipmentServiceImp.activateEquipment(id, user_id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}")
    @Transactional
    public ResponseEntity<EquipmentDetailsDTO> updateEquipment(@PathVariable String id, @RequestBody EquipmentUpdateDTO dados, @RequestParam Long user_id){
        EquipmentDetailsDTO equipmentDetailsDTO = equipmentServiceImp.updateEquipment(id, dados, user_id);
        return ResponseEntity.ok(equipmentDetailsDTO);
    }

}
