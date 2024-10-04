package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.dtos.MainOwner.MainOwnerCreateDTO;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerDetailsDTO;
import com.MapView.BackEnd.serviceImp.MainOwnerServiceImp;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerUpdateDTO;
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
@RequestMapping("/api/v1/mainowner")
@Tag(name = "MainOwner", description = "Operations related to main owners management")
public class MainOwnerController {

    private final MainOwnerServiceImp mainOwnerServiceImp;

    public MainOwnerController(MainOwnerServiceImp mainOwnerServiceImp) {
        this.mainOwnerServiceImp = mainOwnerServiceImp;
    }

    @Operation(summary = "Create a new main owner", description = "Endpoint to create a new main owner in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Main owner successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MainOwnerDetailsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Data validation error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<MainOwnerDetailsDTO> createMainOwner(
            @Parameter(description = "Data transfer object for creating a new main owner", required = true)
            @RequestBody @Valid MainOwnerCreateDTO mainOwnerDTO,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            UriComponentsBuilder uriBuilder) {
        var mainOwner = mainOwnerServiceImp.createMainOwner(mainOwnerDTO, userLog_id);
        var uri = uriBuilder.path("/api/v1/mainowner/{id}").buildAndExpand(mainOwner.id_owner()).toUri();
        return ResponseEntity.created(uri).body(new MainOwnerDetailsDTO(
                mainOwner.id_owner(), mainOwner.costCenter()
        ));
    }

    @Operation(summary = "Retrieve all main owners", description = "Get a paginated list of all main owners in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Main owners list successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<MainOwnerDetailsDTO>> getAllMainOwner(
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            @Parameter(description = "Page number for pagination", required = true)
            @RequestParam int page,
            @Parameter(description = "Number of items per page", required = true)
            @RequestParam int itens) {
        var list = mainOwnerServiceImp.getAllMainOwner(page, itens, userLog_id);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Retrieve a main owner by ID", description = "Get the details of a main owner by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Main owner successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Main owner not found")
    })
    @GetMapping("/{mainowner_id}")
    public ResponseEntity<MainOwnerDetailsDTO> getMainOwner(
            @Parameter(description = "The ID of the main owner to retrieve", required = true)
            @PathVariable String mainowner_id,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var mainOwnerDetailsDTO = mainOwnerServiceImp.getMainOwner(mainowner_id, userLog_id);
        return ResponseEntity.ok(mainOwnerDetailsDTO);
    }

    @Operation(summary = "Update main owner details", description = "Update the details of a main owner by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Main owner successfully updated"),
            @ApiResponse(responseCode = "404", description = "Main owner not found")
    })
    @PutMapping("/{mainowner_id}")
    @Transactional
    public ResponseEntity<MainOwnerDetailsDTO> updateMainOwner(
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            @Parameter(description = "The ID of the main owner to update", required = true)
            @PathVariable String mainowner_id,
            @Parameter(description = "Data transfer object for updating the main owner", required = true)
            @RequestBody @Valid MainOwnerUpdateDTO dados) {
        var updateMainOwner = mainOwnerServiceImp.updateMainOwner(mainowner_id, dados, userLog_id);
        return ResponseEntity.ok(updateMainOwner);
    }

    @Operation(summary = "Inactivate a main owner", description = "Inactivate a main owner by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Main owner successfully inactivated"),
            @ApiResponse(responseCode = "404", description = "Main owner not found")
    })
    @PutMapping("/inactivate/{mainowner_id}")
    @Transactional
    public ResponseEntity<Void> inactivate(
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            @Parameter(description = "The ID of the main owner to inactivate", required = true)
            @PathVariable String mainowner_id) {
        mainOwnerServiceImp.inactivateMainOwner(mainowner_id, userLog_id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate a main owner", description = "Activate a main owner by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Main owner successfully activated"),
            @ApiResponse(responseCode = "404", description = "Main owner not found")
    })
    @PutMapping("/active/{mainowner_id}")
    @Transactional
    public ResponseEntity<Void> active(
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            @Parameter(description = "The ID of the main owner to activate", required = true)
            @PathVariable String mainowner_id) {
        mainOwnerServiceImp.activateMainOwner(mainowner_id, userLog_id);
        return ResponseEntity.ok().build();
    }

}
