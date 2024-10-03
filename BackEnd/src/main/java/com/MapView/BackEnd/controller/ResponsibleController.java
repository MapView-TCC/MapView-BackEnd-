package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.dtos.Area.AreaDetailsDTO;
import com.MapView.BackEnd.serviceImp.ResponsibleServiceImp;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleCrateDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleDetailsDTO;
import com.MapView.BackEnd.dtos.Responsible.ResponsibleUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/ap1/v1/responsible")
@Tag(name = "Responsible", description = "Operations related to responsible management")
public class ResponsibleController {

    private final ResponsibleServiceImp responsibleServiceImp;

    public ResponsibleController(ResponsibleServiceImp responsibleServiceImp) {
        this.responsibleServiceImp = responsibleServiceImp;
    }

    @Operation(summary = "Create a new responsible", description = "Endpoint to create a new responsible entity in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Responsible successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsibleDetailsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Data validation error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<ResponsibleDetailsDTO> createResponsible(
            @Parameter(description = "Data transfer object for creating a new responsible", required = true)
            @RequestBody @Valid ResponsibleCrateDTO data,
            UriComponentsBuilder uriBuilder,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var responsible = responsibleServiceImp.createResposible(data, userLog_id);
        var uri = uriBuilder.path("/ap1/v1/responsible/{id}").buildAndExpand(responsible.responsible_id()).toUri();
        return ResponseEntity.created(uri).body(new ResponsibleDetailsDTO(
                responsible.responsible_id(), responsible.responsible_name(), responsible.edv(), responsible.classes(), responsible.users()
        ));
    }

    @Operation(summary = "Update responsible details", description = "Update the details of a responsible entity by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsible successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsibleDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Responsible not found")
    })
    @PutMapping("/{responsible_id}")
    @Transactional
    public ResponseEntity<ResponsibleDetailsDTO> updateResponsible(
            @Parameter(description = "The ID of the responsible to update", required = true)
            @PathVariable("responsible_id") Long responsible_id,
            @Parameter(description = "Data transfer object for updating the responsible", required = true)
            @RequestBody @Valid ResponsibleUpdateDTO data,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var responsible = responsibleServiceImp.updateResposible(responsible_id, data, userLog_id);
        return ResponseEntity.ok(responsible);
    }

    @Operation(summary = "Get responsible details", description = "Retrieve the details of a responsible entity by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsible successfully retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponsibleDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Responsible not found")
    })
    @GetMapping("/{responsible_id}")
    public ResponseEntity<ResponsibleDetailsDTO> getReponsible(
            @Parameter(description = "The ID of the responsible to retrieve", required = true)
            @PathVariable("responsible_id") Long responsible_id,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var responsible = responsibleServiceImp.getResposibleById(responsible_id, userLog_id);
        return ResponseEntity.ok(responsible);
    }

    @Operation(summary = "Get all responsibles", description = "Retrieve a paginated list of all responsibles in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsibles list successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<ResponsibleDetailsDTO>> getAllReponsible(
            @Parameter(description = "Page number for pagination", required = true)
            @RequestParam int page,
            @Parameter(description = "Number of items per page", required = true)
            @RequestParam int itens,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var responsibles = responsibleServiceImp.getAllResposible(page, itens, userLog_id);
        return ResponseEntity.ok(responsibles);
    }

    @Operation(summary = "Inactivate a responsible", description = "Inactivate a responsible entity by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsible successfully inactivated"),
            @ApiResponse(responseCode = "404", description = "Responsible not found")
    })
    @PutMapping("/inactivate/{responsible_id}")
    @Transactional
    public ResponseEntity<Void> inactivate(
            @Parameter(description = "The ID of the responsible to inactivate", required = true)
            @PathVariable Long responsible_id,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        responsibleServiceImp.inactivateResposible(responsible_id, userLog_id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate a responsible", description = "Activate a responsible entity by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsible successfully activated"),
            @ApiResponse(responseCode = "404", description = "Responsible not found")
    })
    @PutMapping("/active/{responsible_id}")
    @Transactional
    public ResponseEntity<Void> active(
            @Parameter(description = "The ID of the responsible to activate", required = true)
            @PathVariable Long responsible_id,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        responsibleServiceImp.activeResposible(responsible_id, userLog_id);
        return ResponseEntity.ok().build();
    }
}
