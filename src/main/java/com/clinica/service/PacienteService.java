package com.clinica.service;

import java.util.List;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import com.clinica.errors.DuplicateException;
import com.clinica.model.Dentista;
import com.clinica.model.Paciente;
import com.clinica.model.dto.PacienteRequest;
import com.clinica.repository.DentistaRepository;
import com.clinica.repository.PacienteRepository;

@Service
public class PacienteService {

	private final SecurityFilterChain filterChain;

	private final PacienteRepository repository;

	private final DentistaRepository dentistaRepository;

	public PacienteService(SecurityFilterChain filterChain, PacienteRepository repository,
			DentistaRepository dentistaRepository) {
		this.filterChain = filterChain;
		this.repository = repository;
		this.dentistaRepository = dentistaRepository;
	}

	public List<Paciente> listarPacientes() {
		return null;
	}

	public void crearPaciente(PacienteRequest request) {

		var existsEmail = repository.existsByEmail(request.email());
		if (existsEmail) {
			throw new DuplicateException("El email ya se registro");
		}
		Dentista dentista = dentistaRepository.findById(request.idDentista())
				.orElseThrow(() -> new RuntimeException("Dentista no encontrado"));
		repository.save(new Paciente(request, dentista));
	}
}
