package com.MapView.BackEnd.controller;

import com.MapView.BackEnd.serviceImp.ClassesServiceImp;
import com.MapView.BackEnd.dtos.Classes.ClassesCreateDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesDetaiLDTO;
import com.MapView.BackEnd.dtos.Classes.ClassesUpdateDTO;
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
@RequestMapping("/ap1/v1/class")
@Tag(name = "Classes", description = "Operations related to class management")
public class ClassesController {

    private final ClassesServiceImp classesServiceImp;

    public ClassesController(ClassesServiceImp classesServiceImp) {
        this.classesServiceImp = classesServiceImp;
    }

    @Operation(
            summary = "Create a new class.",
            description = "Endpoint to create a new class in the system.",
            externalDocs = @ExternalDocumentation(
                    description = "Learn more about class creation",
                    url = "https://example.com/classes/documentation"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Class successfully created.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClassesDetaiLDTO.class))),
            @ApiResponse(responseCode = "400", description = "Data validation error."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping
    @Transactional
    public ResponseEntity<ClassesDetaiLDTO> createClasses(
            @Parameter(description = "Data transfer object for creating a new class", required = true)
            @RequestBody @Valid ClassesCreateDTO data,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id,
            UriComponentsBuilder uriBuilder) {

        var classes = classesServiceImp.createClasses(data, userLog_id);
        var uri = uriBuilder.path("/ap1/v1/class/{id}").buildAndExpand(classes.id_classes()).toUri();
        return ResponseEntity.created(uri).body(new ClassesDetaiLDTO(classes.id_classes(), classes.enumCourse(), classes.classes(), classes.user(), classes.criation_date()));
    }

    @Operation(summary = "Get class details", description = "Retrieve the details of a class by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClassesDetaiLDTO.class))),
            @ApiResponse(responseCode = "404", description = "Class not found.")
    })
    @GetMapping("/{id_classes}")
    public ResponseEntity<ClassesDetaiLDTO> getClasses(
            @Parameter(description = "The ID of the class to be retrieved", required = true)
            @PathVariable("id_classes") Long id_classes,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var classe = classesServiceImp.getClasseById(id_classes, userLog_id);
        return ResponseEntity.ok(classe);
    }

    @Operation(summary = "Get all classes", description = "Retrieve a paginated list of all classes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes successfully retrieved.")
    })
    @GetMapping
    public ResponseEntity<List<ClassesDetaiLDTO>> getAllClasses(
            @Parameter(description = "Page number for pagination", required = true)
            @RequestParam int page,
            @Parameter(description = "Number of items per page", required = true)
            @RequestParam int itens,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var classes = classesServiceImp.getAllClasses(page, itens, userLog_id);
        return ResponseEntity.ok(classes);
    }

    @Operation(summary = "Update class", description = "Update the details of an existing class by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class successfully updated."),
            @ApiResponse(responseCode = "404", description = "Class not found.")
    })
    @PutMapping("/{id_classes}")
    @Transactional
    public ResponseEntity<ClassesDetaiLDTO> updateClass(
            @Parameter(description = "The ID of the class to be updated", required = true)
            @PathVariable("id_classes") Long id_classes,
            @Parameter(description = "DTO for updating the class", required = true)
            @RequestBody @Valid ClassesUpdateDTO data,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        var classe = classesServiceImp.updateClasses(id_classes, data, userLog_id);
        return ResponseEntity.ok(classe);
    }

    @Operation(summary = "Activate class", description = "Activate an inactive class by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class successfully activated."),
            @ApiResponse(responseCode = "404", description = "Class not found.")
    })
    @PutMapping("/active/{id_classes}")
    @Transactional
    public ResponseEntity<Void> activeClass(
            @Parameter(description = "The ID of the class to be activated", required = true)
            @PathVariable("id_classes") Long id_classes,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        classesServiceImp.activeClass(id_classes, userLog_id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Inactivate class", description = "Inactivate an active class by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class successfully inactivated."),
            @ApiResponse(responseCode = "404", description = "Class not found.")
    })
    @PutMapping("/inactive/{id_classes}")
    @Transactional
    public ResponseEntity<Void> inactiveClass(
            @Parameter(description = "The ID of the class to be inactivated", required = true)
            @PathVariable("id_classes") Long id_classes,
            @Parameter(description = "User log ID for tracking changes", required = true)
            @RequestParam Long userLog_id) {
        classesServiceImp.inactiveClass(id_classes, userLog_id);
        return ResponseEntity.ok().build();
    }
}
