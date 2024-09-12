package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.serviceImp.RaspberryServiceImp;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryCreateDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryDetailsDTO;
import com.MapView.BackEnd.dtos.Raspberry.RaspberryUpdateDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/raspberry")
public class RaspberryController {


    private final RaspberryServiceImp raspberryServiceImp;

    public RaspberryController(RaspberryServiceImp raspberryServiceImp) {
        this.raspberryServiceImp = raspberryServiceImp;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<RaspberryDetailsDTO> createRaspberry(@RequestBody @Valid RaspberryCreateDTO raspberryCreateDTO,@RequestParam Long userLog_id, UriComponentsBuilder uriBuilder){
        var raspberry = raspberryServiceImp.createRaspberry(raspberryCreateDTO, userLog_id);

        // boa pratica, para retornar o caminho
        var uri = uriBuilder.path("/api/v1/raspberry/{id}").buildAndExpand(raspberry.id_raspberry()).toUri();
        return ResponseEntity.created(uri).body(new RaspberryDetailsDTO(raspberry.id_raspberry(), raspberry.building(), raspberry.area()));
    }

    @GetMapping
    public ResponseEntity<List<RaspberryDetailsDTO>> getAllRaspberry(@RequestParam int page, @RequestParam int itens, @RequestParam Long userLog_id){
        var list = raspberryServiceImp.getAllRaspberry(page,itens, userLog_id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id_raspberry}")
    public ResponseEntity<RaspberryDetailsDTO> getIdRaspberry(@PathVariable String id_raspberry, @RequestParam Long userLog_id){
        RaspberryDetailsDTO raspberry = raspberryServiceImp.getRaspberry(id_raspberry, userLog_id);
        return ResponseEntity.ok(raspberry);
    }

    @PutMapping("/{id_raspberry}")
    @Transactional
    public ResponseEntity<RaspberryDetailsDTO> updateRaspberry(@PathVariable String id_raspberry, @RequestBody @Valid RaspberryUpdateDTO dados, @RequestParam Long userLog_id){
        RaspberryDetailsDTO updateRaspberry = raspberryServiceImp.updateRaspberry(id_raspberry, dados, userLog_id);
        return ResponseEntity.ok(updateRaspberry);
    }

    @PutMapping("/inactivate/{id_raspberry}")
    @Transactional
    public ResponseEntity<Void> inactivate(@PathVariable String id_raspberry, @RequestParam Long userLog_id){
        raspberryServiceImp.inactivateRaspberry(id_raspberry, userLog_id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/active/{id_raspberry}")
    @Transactional
    public ResponseEntity<Void> active(@PathVariable String id_raspberry, @RequestParam Long userLog_id){
        raspberryServiceImp.activeRaspberry(id_raspberry, userLog_id);
        return ResponseEntity.ok().build();
    }




}
