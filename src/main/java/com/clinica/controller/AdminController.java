package com.clinica.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.model.dto.CitaResponse;
import com.clinica.model.dto.DentistaResponse;
import com.clinica.model.dto.PacienteResponse;
import com.clinica.model.enums.EstadoCita;
import com.clinica.model.enums.EstadoEntidad;
import com.clinica.service.CitaService;
import com.clinica.service.DentistaService;
import com.clinica.service.PacienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints de administración (solo ROLE_ADMIN)")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final DentistaService dentistaService;
    private final PacienteService pacienteService;
    private final CitaService citaService;

    public AdminController(DentistaService dentistaService, PacienteService pacienteService, CitaService citaService) {
        this.dentistaService = dentistaService;
        this.pacienteService = pacienteService;
        this.citaService = citaService;
        log.info("AdminController inicializado");
    }

    // ==================== DENTISTAS ====================

    @GetMapping("/dentistas")
    @Operation(summary = "Listar todos los dentistas", description = "Lista todos los dentistas incluyendo eliminados. Permite filtrar por estado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de dentistas obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<Page<DentistaResponse>> listarTodosDentistas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) EstadoEntidad estado) {

        log.debug("GET /admin/dentistas - Listando dentistas (admin). Estado: {}", estado);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<DentistaResponse> resultado;
        if (estado != null) {
            resultado = dentistaService.listarDentistasPorEstado(estado, pageable);
            log.info("GET /admin/dentistas - {} dentistas con estado {} retornados", resultado.getNumberOfElements(), estado);
        } else {
            resultado = dentistaService.listarTodosDentistas(pageable);
            log.info("GET /admin/dentistas - {} dentistas totales retornados", resultado.getNumberOfElements());
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/dentistas/{id}")
    @Operation(summary = "Obtener dentista por ID", description = "Obtiene un dentista por ID sin importar su estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dentista encontrado"),
        @ApiResponse(responseCode = "404", description = "Dentista no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<DentistaResponse> obtenerDentistaPorId(@PathVariable Long id) {
        log.debug("GET /admin/dentistas/{} - Obteniendo dentista (admin)", id);
        DentistaResponse dentista = dentistaService.obtenerDentistaSinFiltro(id);
        log.info("GET /admin/dentistas/{} - Dentista encontrado con estado: {}", id, dentista.estado());
        return ResponseEntity.ok(dentista);
    }

    @PatchMapping("/dentistas/{id}/estado")
    @Operation(summary = "Cambiar estado de dentista", description = "Permite cambiar el estado de un dentista (ACTIVO, INACTIVO, ELIMINADO)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Dentista no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<DentistaResponse> cambiarEstadoDentista(
            @PathVariable Long id,
            @RequestParam EstadoEntidad estado) {
        log.info("PATCH /admin/dentistas/{}/estado - Cambiando estado a: {}", id, estado);
        DentistaResponse dentista = dentistaService.cambiarEstado(id, estado);
        log.info("PATCH /admin/dentistas/{}/estado - Estado cambiado exitosamente", id);
        return ResponseEntity.ok(dentista);
    }

    // ==================== PACIENTES ====================

    @GetMapping("/pacientes")
    @Operation(summary = "Listar todos los pacientes", description = "Lista todos los pacientes incluyendo eliminados. Permite filtrar por estado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<Page<PacienteResponse>> listarTodosPacientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) EstadoEntidad estado) {

        log.debug("GET /admin/pacientes - Listando pacientes (admin). Estado: {}", estado);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<PacienteResponse> resultado;
        if (estado != null) {
            resultado = pacienteService.listarPacientesPorEstado(estado, pageable);
            log.info("GET /admin/pacientes - {} pacientes con estado {} retornados", resultado.getNumberOfElements(), estado);
        } else {
            resultado = pacienteService.listarTodosPacientes(pageable);
            log.info("GET /admin/pacientes - {} pacientes totales retornados", resultado.getNumberOfElements());
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/pacientes/{id}")
    @Operation(summary = "Obtener paciente por ID", description = "Obtiene un paciente por ID sin importar su estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<PacienteResponse> obtenerPacientePorId(@PathVariable Long id) {
        log.debug("GET /admin/pacientes/{} - Obteniendo paciente (admin)", id);
        PacienteResponse paciente = pacienteService.obtenerPacienteSinFiltro(id);
        log.info("GET /admin/pacientes/{} - Paciente encontrado con estado: {}", id, paciente.estado());
        return ResponseEntity.ok(paciente);
    }

    @PatchMapping("/pacientes/{id}/estado")
    @Operation(summary = "Cambiar estado de paciente", description = "Permite cambiar el estado de un paciente (ACTIVO, INACTIVO, ELIMINADO)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<PacienteResponse> cambiarEstadoPaciente(
            @PathVariable Long id,
            @RequestParam EstadoEntidad estado) {
        log.info("PATCH /admin/pacientes/{}/estado - Cambiando estado a: {}", id, estado);
        PacienteResponse paciente = pacienteService.cambiarEstado(id, estado);
        log.info("PATCH /admin/pacientes/{}/estado - Estado cambiado exitosamente", id);
        return ResponseEntity.ok(paciente);
    }

    // ==================== CITAS ====================

    @GetMapping("/citas")
    @Operation(summary = "Listar todas las citas", description = "Lista todas las citas incluyendo canceladas. Permite filtrar por estado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<Page<CitaResponse>> listarTodasCitas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) EstadoCita estado) {

        log.debug("GET /admin/citas - Listando citas (admin). Estado: {}", estado);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<CitaResponse> resultado;
        if (estado != null) {
            resultado = citaService.listarCitasPorEstado(estado, pageable);
            log.info("GET /admin/citas - {} citas con estado {} retornadas", resultado.getNumberOfElements(), estado);
        } else {
            resultado = citaService.listarTodasCitas(pageable);
            log.info("GET /admin/citas - {} citas totales retornadas", resultado.getNumberOfElements());
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/citas/{id}")
    @Operation(summary = "Obtener cita por ID", description = "Obtiene una cita por ID sin importar su estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita encontrada"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<CitaResponse> obtenerCitaPorId(@PathVariable Long id) {
        log.debug("GET /admin/citas/{} - Obteniendo cita (admin)", id);
        CitaResponse cita = citaService.obtenerCita(id);
        log.info("GET /admin/citas/{} - Cita encontrada con estado: {}", id, cita.estado());
        return ResponseEntity.ok(cita);
    }

    @PatchMapping("/citas/{id}/estado")
    @Operation(summary = "Cambiar estado de cita", description = "Permite cambiar el estado de una cita (incluso reactivar canceladas)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN")
    })
    public ResponseEntity<CitaResponse> cambiarEstadoCita(
            @PathVariable Long id,
            @RequestParam EstadoCita estado) {
        log.info("PATCH /admin/citas/{}/estado - Cambiando estado a: {}", id, estado);
        CitaResponse cita = citaService.cambiarEstadoAdmin(id, estado);
        log.info("PATCH /admin/citas/{}/estado - Estado cambiado exitosamente", id);
        return ResponseEntity.ok(cita);
    }
}
