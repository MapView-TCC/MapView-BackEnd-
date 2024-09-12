package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.dtos.MainOwner.MainOwnerCreateDTO;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerDetailsDTO;
import com.MapView.BackEnd.serviceImp.MainOwnerServiceImp;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerUpdateDTO;
import com.MapView.BackEnd.infra.NotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mainowner")
public class MainOwnerController {


    private final MainOwnerServiceImp mainOwnerServiceImp;

    public MainOwnerController(MainOwnerServiceImp mainOwnerServiceImp) {
        this.mainOwnerServiceImp = mainOwnerServiceImp;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<MainOwnerDetailsDTO> createMainOwner(@RequestBody @Valid MainOwnerCreateDTO mainOwnerDTO,@RequestParam Long userLog_id, UriComponentsBuilder uriBuilder){
        var mainOwner = mainOwnerServiceImp.createMainOwner(mainOwnerDTO,userLog_id);

        // boa pratica, para retornar o caminho
        var uri = uriBuilder.path("/api/v1/mainowner/{id}").buildAndExpand(mainOwner.id_owner()).toUri();
        return ResponseEntity.created(uri).body(new MainOwnerDetailsDTO(mainOwner.id_owner(), mainOwner.owner_name(), mainOwner.costCenter()));
    }

    @GetMapping
    public ResponseEntity<List<MainOwnerDetailsDTO>> getAllMainOwner(@RequestParam Long userLog_id,@RequestParam int page, @RequestParam int itens){
        var list = mainOwnerServiceImp.getAllMainOwner(page, itens, userLog_id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{mainowner_id}")
    public ResponseEntity<MainOwnerDetailsDTO> getMainOwner(@PathVariable String mainowner_id, @RequestParam Long userLog_id){
        MainOwnerDetailsDTO mainOwnerDetailsDTO = mainOwnerServiceImp.getMainOwner(mainowner_id, userLog_id);
        return ResponseEntity.ok(mainOwnerDetailsDTO);
//        try {
//            MainOwnerDetailsDTO mainOwner = mainOwnerServiceImp.getMainOwner(mainowner_id,userLog_id);
//            return ResponseEntity.ok(mainOwner);
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
    }

    @PutMapping("{mainowner_id}")
    @Transactional
    public ResponseEntity<MainOwnerDetailsDTO> updateMainOwner(@RequestParam Long userLog_id,@PathVariable String mainowner_id, @RequestBody @Valid MainOwnerUpdateDTO dados){
        MainOwnerDetailsDTO updateMainOwner = mainOwnerServiceImp.updateMainOwner(mainowner_id, dados,userLog_id);
        return ResponseEntity.ok(updateMainOwner);
    }

    @PutMapping("/inactivate/{mainowner_id}")
    @Transactional
    public ResponseEntity<Void> inactivate(@RequestParam Long userLog_id,@PathVariable String mainowner_id){
        mainOwnerServiceImp.inactivateMainOwner(mainowner_id,userLog_id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/active/{mainowner_id}")
    @Transactional
    public ResponseEntity<Void> active(@RequestParam Long userLog_id,@PathVariable String mainowner_id){
        mainOwnerServiceImp.activateMainOwner(mainowner_id, userLog_id);
        return ResponseEntity.ok().build();
    }

}
