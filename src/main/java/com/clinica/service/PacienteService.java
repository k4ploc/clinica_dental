package com.clinica.service;

import java.util.List;
import java.util.stream.Collectors;

import com.clinica.errors.ResourceNotFoundException;
import com.clinica.model.dto.PacienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinica.errors.DuplicateException;
import com.clinica.model.Dentista;
import com.clinica.model.Paciente;
import com.clinica.model.dto.PacienteRequest;
import com.clinica.repository.DentistaRepository;
import com.clinica.repository.PacienteRepository;

@Service
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    private final PacienteRepository repository;

    private final DentistaRepository dentistaRepository;

    public PacienteService(PacienteRepository repository,
            DentistaRepository dentistaRepository) {
        this.repository = repository;
        this.dentistaRepository = dentistaRepository;
        log.info("PacienteService inicializado correctamente");
    }

    @Transactional(readOnly = true)
    public List<PacienteResponse> listarPacientes() {
        log.debug("Obteniendo lista completa de pacientes");
        var lista = repository.findAll();
        log.info("Se encontraron {} pacientes", lista.size());
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponse> listarPacientesPaginados(Pageable pageable) {
        log.debug("Obteniendo pacientes paginados: página {}, tamaño {}, ordenamiento {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Paciente> page = repository.findAll(pageable);
        log.info("Se retornan {} pacientes de la página {} (total: {} registros)",
                page.getNumberOfElements(), page.getNumber(), page.getTotalElements());
        return page.map(this::toResponse);
    }

    @Transactional
    public PacienteResponse crearPaciente(PacienteRequest request) {
        log.info("Iniciando creación de nuevo paciente: {} {}", request.nombre(), request.apellido());

        log.debug("Verificando si el email ya existe: {}", request.email());
        var existsEmail = repository.existsByEmail(request.email());
        if (existsEmail) {
            log.warn("Intento de crear paciente con email duplicado: {}", request.email());
            throw new DuplicateException("El email ya se registro");
        }

        log.debug("Buscando dentista asignado con ID: {}", request.idDentista());
        Dentista dentista = dentistaRepository.findById(request.idDentista())
                .orElseThrow(() -> {
                    log.error("Dentista no encontrado para asignar al paciente con ID: {}", request.idDentista());
                    return new ResourceNotFoundException("Dentista", request.idDentista());
                });

        Paciente paciente = repository.save(new Paciente(request, dentista));
        log.info("Paciente creado exitosamente con ID: {}, asignado a dentista: {} {}",
                paciente.getId(), dentista.getNombre(), dentista.getApellido());
        log.debug("Detalles del paciente creado: {}", paciente);
        return toResponse(paciente);
    }

    // Mapea Paciente -> PacienteResponse
    private PacienteResponse toResponse(Paciente p) {
        if (p == null) return null;
        return new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getTelefono(), p.getEmail());
    }

    @Transactional(readOnly = true)
    public PacienteResponse obtenerPaciente(Long id) {
        log.debug("Buscando paciente con ID: {}", id);
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Paciente", id);
                });
        log.info("Paciente encontrado: {} {} (ID: {})", paciente.getNombre(), paciente.getApellido(), id);
        return toResponse(paciente);
    }

    @Transactional
    public PacienteResponse actualizarPaciente(Long id, PacienteRequest request) {
        log.info("Iniciando actualización de paciente con ID: {}", id);
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Paciente a actualizar no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Paciente", id);
                });

        log.debug("Datos actuales del paciente: {} {}, email: {}",
                paciente.getNombre(), paciente.getApellido(), paciente.getEmail());

        // Verificar que el email no esté duplicado si cambió
        if (!paciente.getEmail().equals(request.email()) && repository.existsByEmail(request.email())) {
            log.warn("Intento de actualizar paciente con email duplicado: {}", request.email());
            throw new DuplicateException("El email ya está registrado");
        }

        log.debug("Buscando dentista asignado con ID: {}", request.idDentista());
        Dentista dentista = dentistaRepository.findById(request.idDentista())
                .orElseThrow(() -> {
                    log.error("Dentista no encontrado para asignar al paciente con ID: {}", request.idDentista());
                    return new ResourceNotFoundException("Dentista", request.idDentista());
                });

        paciente.setNombre(request.nombre());
        paciente.setApellido(request.apellido());
        paciente.setTelefono(request.telefono());
        paciente.setEmail(request.email());
        paciente.setDentista(dentista);

        Paciente actualizado = repository.save(paciente);
        log.info("Paciente actualizado exitosamente: {} {} (ID: {})",
                actualizado.getNombre(), actualizado.getApellido(), id);
        return toResponse(actualizado);
    }

    @Transactional
    public void eliminarPaciente(Long id) {
        log.info("Iniciando eliminación de paciente con ID: {}", id);
        if (!repository.existsById(id)) {
            log.warn("Intento de eliminar paciente no existente con ID: {}", id);
            throw new ResourceNotFoundException("Paciente", id);
        }
        repository.deleteById(id);
        log.info("Paciente eliminado exitosamente con ID: {}", id);
    }

}
