package com.clinica.service;

import java.util.List;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import com.clinica.model.Paciente;

@Service
public class PacienteService {

	private final SecurityFilterChain filterChain;

	public PacienteService(SecurityFilterChain filterChain) {
		this.filterChain = filterChain;
	}

	public List<Paciente> listarPacientes() {
		return null;
	}

	public void crearPaciente(Paciente p) {
	}
}
