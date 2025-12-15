package com.clinica.service;

import java.util.List;
import java.util.stream.Collectors;

import com.clinica.model.dto.PacienteResponse;
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

    private final PacienteRepository repository;

    private final DentistaRepository dentistaRepository;

    public PacienteService(PacienteRepository repository,
            DentistaRepository dentistaRepository) {
        this.repository = repository;
        this.dentistaRepository = dentistaRepository;
    }

    public List<PacienteResponse> listarPacientes() {
        var lista = repository.findAll();
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PacienteResponse crearPaciente(PacienteRequest request) {

        var existsEmail = repository.existsByEmail(request.email());
        if (existsEmail) {
            throw new DuplicateException("El email ya se registro");
        }
        Dentista dentista = dentistaRepository.findById(request.idDentista())
                .orElseThrow(() -> new RuntimeException("Dentista no encontrado"));
        Paciente paciente = repository.save(new Paciente(request, dentista));
        return toResponse(paciente);
    }

    // Mapea Paciente -> PacienteResponse
    private PacienteResponse toResponse(Paciente p) {
        if (p == null) return null;
        return new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getTelefono(), p.getEmail());
    }

    public PacienteResponse obtenerPaciente(Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
        return toResponse(paciente);
    }

    public PacienteResponse actualizarPaciente(Long id, PacienteRequest request) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));

        // Verificar que el email no esté duplicado si cambió
        if (!paciente.getEmail().equals(request.email()) && repository.existsByEmail(request.email())) {
            throw new DuplicateException("El email ya está registrado");
        }

        Dentista dentista = dentistaRepository.findById(request.idDentista())
                .orElseThrow(() -> new RuntimeException("Dentista no encontrado"));

        paciente.setNombre(request.nombre());
        paciente.setApellido(request.apellido());
        paciente.setTelefono(request.telefono());
        paciente.setEmail(request.email());
        paciente.setDentista(dentista);

        Paciente actualizado = repository.save(paciente);
        return toResponse(actualizado);
    }

    public void eliminarPaciente(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Paciente no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

}
