package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.entities.Area;
import com.MapView.BackEnd.infra.Exceptions.ValidationExceptionHandler;
import com.MapView.BackEnd.serviceImp.AreaServiceImp;
import com.MapView.BackEnd.dtos.Area.AreaCreateDTO;
import com.MapView.BackEnd.dtos.Area.AreaDetailsDTO;
import com.MapView.BackEnd.dtos.Area.AreaUpdateDTO;
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
@RequestMapping("/api/v1/area")
@Tag(name = "Area", description = "Operations related to area management")
public class AreaController {

    private final AreaServiceImp areaServiceImp;

    public AreaController(AreaServiceImp areaServiceImp) {
        this.areaServiceImp = areaServiceImp;
    }

    @Operation(
            summary = "Create a new area.",
            description = "Endpoint to create a new area in the system.",
            externalDocs = @ExternalDocumentation(
                    description = "Learn more about area creation",
                    url = "https://exemplo.com/areas/documentation"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Area successfully saved.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Area.class))),
            @ApiResponse(responseCode = "200", description = "Area successfully retrieved.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Area.class))),
            @ApiResponse(responseCode = "417", description = "Data validation error.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationExceptionHandler.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping(produces = "application/json", consumes = "application/json")
    @Transactional
    public ResponseEntity<AreaDetailsDTO> createArea(
            @Parameter(description = "Data transfer object for creating a new area", required = true)
            @RequestBody @Valid AreaCreateDTO areaCreateDTO,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            UriComponentsBuilder uriBuilder) {

        var area = areaServiceImp.createArea(areaCreateDTO, userLog_id);
        var uri = uriBuilder.path("/api/v1/area/{id}").buildAndExpand(area.id_area()).toUri();
        return ResponseEntity.created(uri).body(new AreaDetailsDTO(area.id_area(), area.area_code(), area.area_name()));
    }

    @Operation(summary = "Get area details", description = "Retrieve the details of an area by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AreaDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Area not found.")
    })
    @GetMapping("/{id_area}")
    public ResponseEntity<AreaDetailsDTO> getArea(
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            @Parameter(description = "The ID of the area to be retrieved", required = true)
            @PathVariable Long id_area) {
        var area = areaServiceImp.getArea(id_area, userLog_id);
        return ResponseEntity.ok(area);
    }

    @Operation(summary = "Get all areas", description = "Retrieve a paginated list of all areas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Areas successfully retrieved.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AreaDetailsDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<AreaDetailsDTO>> getAllArea(
            @Parameter(description = "Page number for pagination", required = true)
            @RequestParam int page,
            @Parameter(description = "Number of items per page", required = true)
            @RequestParam int items,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var areas = areaServiceImp.getAllArea(page, items, userLog_id);
        return ResponseEntity.ok(areas);
    }

    @Operation(summary = "Update area", description = "Update the details of an existing area by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area successfully updated.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AreaDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Area not found.")
    })
    @PutMapping("/{id_area}")
    @Transactional
    public ResponseEntity<AreaDetailsDTO> updateArea(
            @Parameter(description = "The ID of the area to be updated", required = true)
            @PathVariable Long id_area,
            @Parameter(description = "DTO for updating the area", required = true)
            @RequestBody AreaUpdateDTO areaUpdateDTO,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var updatedArea = areaServiceImp.updateArea(id_area, areaUpdateDTO, userLog_id);
        return ResponseEntity.ok(updatedArea);
    }

    @Operation(summary = "Inactivate area", description = "Inactivate an existing area by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area successfully inactivated."),
            @ApiResponse(responseCode = "404", description = "Area not found.")
    })
    @PutMapping("/inactivate/{id_area}")
    @Transactional
    public ResponseEntity<Void> inactivateArea(
            @Parameter(description = "The ID of the area to be inactivated", required = true)
            @PathVariable Long id_area,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        areaServiceImp.inactivateArea(id_area, userLog_id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate area", description = "Activate an inactivated area by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area successfully activated."),
            @ApiResponse(responseCode = "404", description = "Area not found.")
    })
    @PutMapping("/activate/{id_area}")
    @Transactional
    public ResponseEntity<Void> activateArea(
            @Parameter(description = "The ID of the area to be activated", required = true)
            @PathVariable Long id_area,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        areaServiceImp.activateArea(id_area, userLog_id);
        return ResponseEntity.ok().build();
    }
}
