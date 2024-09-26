package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.dtos.Building.BuildingCreateDTO;
import com.MapView.BackEnd.dtos.Building.BuildingDetailsDTO;
import com.MapView.BackEnd.dtos.Building.BuildingUpdateDTO;
import com.MapView.BackEnd.entities.Building;
import com.MapView.BackEnd.infra.Exceptions.ValidationExceptionHandler;
import com.MapView.BackEnd.serviceImp.BuildingServiceImp;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
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
@RequestMapping("/api/v1/building")
@Tag(name = "Building", description = "Operations related to building management")
public class BuildingController {

    private final BuildingServiceImp buildingServiceImp;

    public BuildingController(BuildingServiceImp buildingServiceImp) {
        this.buildingServiceImp = buildingServiceImp;
    }

    @Operation(
            summary = "Create a new building.",
            description = "Endpoint to create a new building in the system.",
            externalDocs = @ExternalDocumentation(
                    description = "Learn more about building creation",
                    url = "https://exemplo.com/buildings/documentation"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Building successfully created.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Building.class))),
            @ApiResponse(responseCode = "400", description = "Data validation error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationExceptionHandler.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping(produces = "application/json", consumes = "application/json")
    @Transactional
    public ResponseEntity<BuildingDetailsDTO> createBuilding(
            @Parameter(description = "Data transfer object for creating a new building", required = true)
            @RequestBody @Valid BuildingCreateDTO buildingCreateDTO,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            UriComponentsBuilder uriBuilder) {

        var building = buildingServiceImp.createBuilding(buildingCreateDTO, userLog_id);
        var uri = uriBuilder.path("/api/v1/building/{id}").buildAndExpand(building.id_building()).toUri();
        return ResponseEntity.created(uri).body(new BuildingDetailsDTO(building.id_building(),building.building_code()));
    }

    @Operation(summary = "Get building details", description = "Retrieve the details of a building by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Building found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BuildingDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Building not found.")
    })
    @GetMapping("/{id_building}")
    public ResponseEntity<BuildingDetailsDTO> getBuilding(
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            @Parameter(description = "The ID of the building to be retrieved", required = true)
            @PathVariable Long id_building) {
        var building = buildingServiceImp.getBuilding(id_building, userLog_id);
        return ResponseEntity.ok(building);
    }

    @Operation(summary = "Get all buildings", description = "Retrieve a paginated list of all buildings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buildings successfully retrieved.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BuildingDetailsDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<BuildingDetailsDTO>> getAllBuildings(
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var buildings = buildingServiceImp.getAllBuilding(userLog_id);
        return ResponseEntity.ok(buildings);
    }

    @Operation(summary = "Update building", description = "Update the details of an existing building by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Building successfully updated.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BuildingDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Building not found.")
    })
    @PutMapping("/{id_building}")
    @Transactional
    public ResponseEntity<BuildingDetailsDTO> updateBuilding(
            @Parameter(description = "The ID of the building to be updated", required = true)
            @PathVariable Long id_building,
            @Parameter(description = "DTO for updating the building", required = true)
            @RequestBody BuildingUpdateDTO buildingUpdateDTO,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var updatedBuilding = buildingServiceImp.updateBuilding(id_building, buildingUpdateDTO, userLog_id);
        return ResponseEntity.ok(updatedBuilding);
    }

    @Operation(summary = "Inactivate building", description = "Inactivate an existing building by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Building successfully inactivated."),
            @ApiResponse(responseCode = "404", description = "Building not found.")
    })
    @PutMapping("/inactivate/{id_building}")
    @Transactional
    public ResponseEntity<Void> inactivateBuilding(
            @Parameter(description = "The ID of the building to be inactivated", required = true)
            @PathVariable Long id_building,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        buildingServiceImp.inactivateBuilding(id_building, userLog_id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate building", description = "Activate an inactivated building by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Building successfully activated."),
            @ApiResponse(responseCode = "404", description = "Building not found.")
    })
    @PutMapping("/activate/{id_building}")
    @Transactional
    public ResponseEntity<Void> activateBuilding(
            @Parameter(description = "The ID of the building to be activated", required = true)
            @PathVariable Long id_building,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        buildingServiceImp.activateBuilding(id_building, userLog_id);
        return ResponseEntity.ok().build();
    }
}
