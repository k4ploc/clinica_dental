package com.clinica.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.model.Paciente;
import com.clinica.service.PacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

	private final PacienteService pacienteService;

	public PacienteController(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}

	@GetMapping
	public ResponseEntity<?> listPacientes() {
		List<Paciente> response = pacienteService.listarPacientes();
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public String crearPaciente(@RequestBody Paciente paciente) {
		pacienteService.crearPaciente(paciente);
		return "✅ Paciente creado con ID: " + paciente.getId();
	}

	@GetMapping("/{id}")
	public Map<String, Object> obtenerPaciente(@PathVariable String id) {
		return null;
	}

	@DeleteMapping("/{id}")
	public String eliminarPaciente(@PathVariable String id) {

		return "🗑️ Paciente eliminado con ID: " + id;
	}
}
