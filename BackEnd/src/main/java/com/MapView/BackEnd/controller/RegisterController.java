package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.dtos.EquipmentResponsible.EquipmentResponsibleCreateDTO;
import com.MapView.BackEnd.dtos.Location.LocationCreateDTO;
import com.MapView.BackEnd.dtos.Register.RegisterCreateDTO;
import com.MapView.BackEnd.dtos.Register.RegisterDetailsDTO;
import com.MapView.BackEnd.dtos.Register.TesteDTO;
import com.MapView.BackEnd.serviceImp.RegisterServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/register")
@Tag(name = "Register", description = "Operations related to equipment, location, and responsible registration")
public class RegisterController {


    private final RegisterServiceImp registerServiceImp;

    public RegisterController(RegisterServiceImp registerServiceImp) {
        this.registerServiceImp = registerServiceImp;
    }

    @Operation(
            summary = "Register new equipment, location, and responsible",
            description = "Create a new equipment, its location, and assign a responsible person. Requires user ID for logging.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registration successful.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterDetailsDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Data validation error."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @PostMapping
    @Transactional
    public ResponseEntity<RegisterDetailsDTO> register(@RequestBody @Valid RegisterCreateDTO dataRegister, @RequestParam Long userLog_id){
        System.out.println(dataRegister.id_equipment());
        RegisterDetailsDTO  register = registerServiceImp.register(dataRegister,userLog_id);
        return ResponseEntity.ok(register);

    }

}
