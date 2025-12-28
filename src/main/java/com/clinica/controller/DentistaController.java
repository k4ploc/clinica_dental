package com.clinica.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinica.model.Dentista;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.dto.DentistaResponse;
import com.clinica.service.DentistaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dentista")
@Tag(name = "Dentistas", description = "Operaciones relacionadas con dentistas")
public class DentistaController {

    private static final Logger log = LoggerFactory.getLogger(DentistaController.class);

    private final DentistaService service;

    public DentistaController(DentistaService service) {
        this.service = service;
        log.info("DentistaController inicializado");
    }

    @GetMapping
    @Operation(summary = "Listar dentistas", description = "Obtiene una lista paginada de todos los dentistas registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de dentistas obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<DentistaResponse>> getDentistas(
            @Parameter(description = "Número de página (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento (ej: nombre, apellido)", example = "nombre")
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Dirección de ordenamiento", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            org.springframework.data.domain.Sort.Direction.fromString(direction),
            sort);
        log.debug("GET /dentista - Listando dentistas con paginación");
        Page<DentistaResponse> result = service.getDentistasPaginados(pageable);
        if (result != null) {
            log.info("GET /dentista - {} dentistas retornados", result.getNumberOfElements());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener dentista por ID", description = "Obtiene los detalles de un dentista específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dentista encontrado"),
        @ApiResponse(responseCode = "404", description = "Dentista no encontrado")
    })
    public ResponseEntity<DentistaResponse> obtenerDentista(
            @Parameter(description = "ID del dentista", required = true)
            @PathVariable Long id) {
        log.debug("GET /dentista/{} - Obteniendo dentista específico", id);
        DentistaResponse response = service.obtenerDentista(id);
        log.info("GET /dentista/{} - Dentista obtenido exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo dentista", description = "Crea un nuevo registro de dentista en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dentista creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    public ResponseEntity<DentistaResponse> createDentista(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del nuevo dentista",
                required = true,
                content = @Content(schema = @Schema(implementation = DentistaRequest.class)))
            @Valid @RequestBody DentistaRequest request) {
        log.debug("POST /dentista - Creando nuevo dentista: {} {}", request.nombre(), request.apellido());
        Dentista newDentista = service.createDentista(request);
        DentistaResponse response = new DentistaResponse(newDentista.getId(), newDentista.getNombre(), newDentista.getApellido(), newDentista.getTelefono(), newDentista.getEspecialidad() != null ? newDentista.getEspecialidad().name() : null, null, newDentista.getEstado());
        log.info("POST /dentista - Dentista creado exitosamente con ID: {}", newDentista.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar dentista", description = "Actualiza los datos de un dentista existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dentista actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Dentista no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    public ResponseEntity<DentistaResponse> actualizarDentista(
            @Parameter(description = "ID del dentista", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos actualizados del dentista",
                required = true,
                content = @Content(schema = @Schema(implementation = DentistaRequest.class)))
            @Valid @RequestBody DentistaRequest request) {
        log.debug("PUT /dentista/{} - Actualizando dentista", id);
        DentistaResponse response = service.actualizarDentista(id, request);
        log.info("PUT /dentista/{} - Dentista actualizado exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar dentista", description = "Elimina un dentista del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dentista eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Dentista no encontrado")
    })
    public ResponseEntity<Void> eliminarDentista(
            @Parameter(description = "ID del dentista", required = true)
            @PathVariable Long id) {
        log.debug("DELETE /dentista/{} - Eliminando dentista", id);
        service.eliminarDentista(id);
        log.info("DELETE /dentista/{} - Dentista eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }

}
