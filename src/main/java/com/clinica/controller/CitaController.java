package com.clinica.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.model.dto.CitaRequest;
import com.clinica.model.dto.CitaResponse;
import com.clinica.model.enums.EstadoCita;
import com.clinica.service.CitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Operaciones relacionadas con citas medicas")
public class CitaController {

    private static final Logger log = LoggerFactory.getLogger(CitaController.class);
    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
        log.info("CitaController inicializado");
    }

    @GetMapping
    @Operation(summary = "Listar citas", description = "Obtiene una lista paginada de todas las citas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parametros de paginacion invalidos")
    })
    public ResponseEntity<Page<CitaResponse>> listarCitas(
            @Parameter(description = "Numero de pagina (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanio de pagina", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento", example = "fecha")
            @RequestParam(defaultValue = "fecha") String sort,
            @Parameter(description = "Direccion de ordenamiento", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sort);
        log.debug("GET /api/citas - Listando citas con paginacion");
        Page<CitaResponse> result = citaService.listarCitasPaginadas(pageable);
        log.info("GET /api/citas - {} citas retornadas", result.getNumberOfElements());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por ID", description = "Obtiene los detalles de una cita especifica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita encontrada"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<CitaResponse> obtenerCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id) {
        log.debug("GET /api/citas/{} - Obteniendo cita especifica", id);
        CitaResponse response = citaService.obtenerCita(id);
        log.info("GET /api/citas/{} - Cita obtenida exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear nueva cita", description = "Crea una nueva cita en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cita creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o conflicto de horario"),
        @ApiResponse(responseCode = "404", description = "Paciente o dentista no encontrado")
    })
    public ResponseEntity<CitaResponse> crearCita(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos de la nueva cita",
                required = true,
                content = @Content(schema = @Schema(implementation = CitaRequest.class)))
            @Valid @RequestBody CitaRequest request) {
        log.debug("POST /api/citas - Creando nueva cita para fecha: {}", request.fecha());
        CitaResponse response = citaService.crearCita(request);
        log.info("POST /api/citas - Cita creada exitosamente con ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita", description = "Actualiza los datos de una cita existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cita, paciente o dentista no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o conflicto de horario")
    })
    public ResponseEntity<CitaResponse> actualizarCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos actualizados de la cita",
                required = true,
                content = @Content(schema = @Schema(implementation = CitaRequest.class)))
            @Valid @RequestBody CitaRequest request) {
        log.debug("PUT /api/citas/{} - Actualizando cita", id);
        CitaResponse response = citaService.actualizarCita(id, request);
        log.info("PUT /api/citas/{} - Cita actualizada exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar cita", description = "Cancela una cita (baja logica, no se elimina de la base de datos)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cita cancelada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "400", description = "La cita ya esta cancelada")
    })
    public ResponseEntity<Void> eliminarCita(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id) {
        log.debug("DELETE /api/citas/{} - Cancelando cita (baja logica)", id);
        citaService.eliminarCita(id);
        log.info("DELETE /api/citas/{} - Cita cancelada exitosamente", id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de cita", description = "Actualiza el estado de una cita")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "400", description = "No se puede modificar una cita cancelada o estado invalido")
    })
    public ResponseEntity<CitaResponse> cambiarEstado(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado: PROGRAMADA, CONFIRMADA, EN_CURSO, COMPLETADA, CANCELADA", required = true)
            @RequestParam String estado) {
        log.debug("PATCH /api/citas/{}/estado - Cambiando estado a {}", id, estado);
        EstadoCita nuevoEstado = EstadoCita.from(estado);
        CitaResponse response = citaService.cambiarEstado(id, nuevoEstado);
        log.info("PATCH /api/citas/{}/estado - Estado cambiado a {}", id, nuevoEstado);
        return ResponseEntity.ok(response);
    }

    // === Endpoints adicionales ===

    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Citas por paciente", description = "Obtiene todas las citas de un paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas del paciente")
    })
    public ResponseEntity<List<CitaResponse>> obtenerCitasPorPaciente(
            @Parameter(description = "ID del paciente", required = true)
            @PathVariable Long pacienteId) {
        log.debug("GET /api/citas/paciente/{} - Obteniendo citas del paciente", pacienteId);
        List<CitaResponse> citas = citaService.obtenerCitasPorPaciente(pacienteId);
        log.info("GET /api/citas/paciente/{} - {} citas encontradas", pacienteId, citas.size());
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/dentista/{dentistaId}")
    @Operation(summary = "Citas por dentista", description = "Obtiene todas las citas de un dentista")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas del dentista")
    })
    public ResponseEntity<List<CitaResponse>> obtenerCitasPorDentista(
            @Parameter(description = "ID del dentista", required = true)
            @PathVariable Long dentistaId) {
        log.debug("GET /api/citas/dentista/{} - Obteniendo citas del dentista", dentistaId);
        List<CitaResponse> citas = citaService.obtenerCitasPorDentista(dentistaId);
        log.info("GET /api/citas/dentista/{} - {} citas encontradas", dentistaId, citas.size());
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/rango")
    @Operation(summary = "Citas en rango de fechas", description = "Obtiene citas entre dos fechas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas en el rango")
    })
    public ResponseEntity<List<CitaResponse>> obtenerCitasEntreFechas(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.debug("GET /api/citas/rango - Buscando citas entre {} y {}", inicio, fin);
        List<CitaResponse> citas = citaService.obtenerCitasEntreFechas(inicio, fin);
        log.info("GET /api/citas/rango - {} citas encontradas", citas.size());
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/paciente/{pacienteId}/futuras")
    @Operation(summary = "Citas futuras del paciente", description = "Obtiene las proximas citas de un paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas futuras")
    })
    public ResponseEntity<List<CitaResponse>> obtenerCitasFuturasPaciente(
            @Parameter(description = "ID del paciente", required = true)
            @PathVariable Long pacienteId) {
        log.debug("GET /api/citas/paciente/{}/futuras - Obteniendo citas futuras", pacienteId);
        List<CitaResponse> citas = citaService.obtenerCitasFuturasPaciente(pacienteId);
        log.info("GET /api/citas/paciente/{}/futuras - {} citas futuras encontradas", pacienteId, citas.size());
        return ResponseEntity.ok(citas);
    }
}
