package com.clinica.controller;

import java.util.List;

import com.clinica.model.dto.PacienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.model.dto.PacienteRequest;
import com.clinica.service.PacienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

	private static final Logger log = LoggerFactory.getLogger(PacienteController.class);
	private final PacienteService pacienteService;

	public PacienteController(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
		log.info("PacienteController inicializado");
	}

	@GetMapping
	public ResponseEntity<Page<PacienteResponse>> listPacientes(Pageable pageable) {
		log.debug("GET /pacientes - Listando pacientes con paginación");
		Page<PacienteResponse> page = pacienteService.listarPacientesPaginados(pageable);
		if (page != null) {
			log.info("GET /pacientes - {} pacientes retornados", page.getNumberOfElements());
		}
		return ResponseEntity.ok(page);
	}

	@PostMapping
	public ResponseEntity<PacienteResponse> crearPaciente(@Valid @RequestBody PacienteRequest request) {
		log.debug("POST /pacientes - Creando nuevo paciente: {} {}", request.nombre(), request.apellido());
		PacienteResponse response = pacienteService.crearPaciente(request);
		log.info("POST /pacientes - Paciente creado exitosamente con ID: {}", response.id());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PacienteResponse> obtenerPaciente(@PathVariable Long id) {
		log.debug("GET /pacientes/{} - Obteniendo paciente específico", id);
		PacienteResponse response = pacienteService.obtenerPaciente(id);
		log.info("GET /pacientes/{} - Paciente obtenido exitosamente", id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PacienteResponse> actualizarPaciente(
			@PathVariable Long id,
			@Valid @RequestBody PacienteRequest request) {
		log.debug("PUT /pacientes/{} - Actualizando paciente", id);
		PacienteResponse response = pacienteService.actualizarPaciente(id, request);
		log.info("PUT /pacientes/{} - Paciente actualizado exitosamente", id);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
		log.debug("DELETE /pacientes/{} - Eliminando paciente", id);
		pacienteService.eliminarPaciente(id);
		log.info("DELETE /pacientes/{} - Paciente eliminado exitosamente", id);
		return ResponseEntity.noContent().build();
	}
}
