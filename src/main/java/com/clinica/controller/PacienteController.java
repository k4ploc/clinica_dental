package com.clinica.controller;

import com.clinica.model.dto.PacienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.model.dto.PacienteRequest;
import com.clinica.service.PacienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Operaciones relacionadas con pacientes")
public class PacienteController {

	private static final Logger log = LoggerFactory.getLogger(PacienteController.class);
	private final PacienteService pacienteService;

	public PacienteController(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
		log.info("PacienteController inicializado");
	}

	@GetMapping
	@Operation(summary = "Listar pacientes", description = "Obtiene una lista paginada de todos los pacientes registrados")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente"),
		@ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
	})
	public ResponseEntity<Page<PacienteResponse>> listPacientes(
			@Parameter(description = "Número de página (0-indexed)", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Tamaño de página", example = "10")
			@RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Campo de ordenamiento (ej: nombre, apellido)", example = "nombre")
			@RequestParam(defaultValue = "id") String sort,
			@Parameter(description = "Dirección de ordenamiento", example = "asc")
			@RequestParam(defaultValue = "asc") String direction) {
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sort);
		log.debug("GET /pacientes - Listando pacientes con paginación");
		Page<PacienteResponse> result = pacienteService.listarPacientesPaginados(pageable);
		if (result != null) {
			log.info("GET /pacientes - {} pacientes retornados", result.getNumberOfElements());
		}
		return ResponseEntity.ok(result);
	}

	@PostMapping
	@Operation(summary = "Crear nuevo paciente", description = "Crea un nuevo registro de paciente en el sistema")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Paciente creado exitosamente"),
		@ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
	})
	public ResponseEntity<PacienteResponse> crearPaciente(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
				description = "Datos del nuevo paciente",
				required = true,
				content = @Content(schema = @Schema(implementation = PacienteRequest.class)))
			@Valid @RequestBody PacienteRequest request) {
		log.debug("POST /pacientes - Creando nuevo paciente: {} {}", request.nombre(), request.apellido());
		PacienteResponse response = pacienteService.crearPaciente(request);
		log.info("POST /pacientes - Paciente creado exitosamente con ID: {}", response.id());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener paciente por ID", description = "Obtiene los detalles de un paciente específico por su ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Paciente encontrado"),
		@ApiResponse(responseCode = "404", description = "Paciente no encontrado")
	})
	public ResponseEntity<PacienteResponse> obtenerPaciente(
			@Parameter(description = "ID del paciente", required = true)
			@PathVariable Long id) {
		log.debug("GET /pacientes/{} - Obteniendo paciente específico", id);
		PacienteResponse response = pacienteService.obtenerPaciente(id);
		log.info("GET /pacientes/{} - Paciente obtenido exitosamente", id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar paciente", description = "Actualiza los datos de un paciente existente")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente"),
		@ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
		@ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
	})
	public ResponseEntity<PacienteResponse> actualizarPaciente(
			@Parameter(description = "ID del paciente", required = true)
			@PathVariable Long id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
				description = "Datos actualizados del paciente",
				required = true,
				content = @Content(schema = @Schema(implementation = PacienteRequest.class)))
			@Valid @RequestBody PacienteRequest request) {
		log.debug("PUT /pacientes/{} - Actualizando paciente", id);
		PacienteResponse response = pacienteService.actualizarPaciente(id, request);
		log.info("PUT /pacientes/{} - Paciente actualizado exitosamente", id);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar paciente", description = "Elimina un paciente del sistema")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Paciente eliminado exitosamente"),
		@ApiResponse(responseCode = "404", description = "Paciente no encontrado")
	})
	public ResponseEntity<Void> eliminarPaciente(
			@Parameter(description = "ID del paciente", required = true)
			@PathVariable Long id) {
		log.debug("DELETE /pacientes/{} - Eliminando paciente", id);
		pacienteService.eliminarPaciente(id);
		log.info("DELETE /pacientes/{} - Paciente eliminado exitosamente", id);
		return ResponseEntity.noContent().build();
	}
}
