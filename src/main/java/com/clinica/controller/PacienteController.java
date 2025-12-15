package com.clinica.controller;

import java.util.List;

import com.clinica.model.dto.PacienteResponse;
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

	private final PacienteService pacienteService;

	public PacienteController(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}

	@GetMapping
	public ResponseEntity<List<PacienteResponse>> listPacientes() {
		List<PacienteResponse> response = pacienteService.listarPacientes();
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<PacienteResponse> crearPaciente(@Valid @RequestBody PacienteRequest request) {
		PacienteResponse response = pacienteService.crearPaciente(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PacienteResponse> obtenerPaciente(@PathVariable Long id) {
		PacienteResponse response = pacienteService.obtenerPaciente(id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PacienteResponse> actualizarPaciente(
			@PathVariable Long id,
			@Valid @RequestBody PacienteRequest request) {
		PacienteResponse response = pacienteService.actualizarPaciente(id, request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
		pacienteService.eliminarPaciente(id);
		return ResponseEntity.noContent().build();
	}
}
