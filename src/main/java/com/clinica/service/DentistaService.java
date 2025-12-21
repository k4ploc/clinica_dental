package com.clinica.service;

import com.clinica.controller.PacienteController;
import com.clinica.errors.ResourceNotFoundException;
import com.clinica.model.dto.DentistaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinica.model.Dentista;
import com.clinica.model.Paciente;
import com.clinica.model.dto.DentistaResponse;
import com.clinica.model.dto.PacienteResponse;
import com.clinica.model.enums.Especialidad;
import com.clinica.repository.DentistaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DentistaService {

    private static final Logger log = LoggerFactory.getLogger(PacienteController.class);

    private final DentistaRepository repository;

    public DentistaService(DentistaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public Dentista createDentista(DentistaRequest request) {
        return repository.save(new Dentista(request));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "dentistas")
    public List<DentistaResponse> getDentistas() {
        List<Dentista> lista = repository.findAll();
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<DentistaResponse> getDentistasPaginados(Pageable pageable) {
        Page<Dentista> page = repository.findAll(pageable);
        return page.map(this::toResponse);
    }

    private DentistaResponse toResponse(Dentista d) {
        List<PacienteResponse> pacientes = null;
        if (d.getPacientes() != null) {
            pacientes = d.getPacientes().stream()
                    .map(p -> new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getTelefono(), p.getEmail()))
                    .collect(Collectors.toList());
        }
        String especialidad = d.getEspecialidad() != null ? d.getEspecialidad().name() : null;
        return new DentistaResponse(d.getId(), d.getNombre(), d.getApellido(), d.getTelefono(), especialidad, pacientes);
    }

    @Transactional(readOnly = true)
    public DentistaResponse obtenerDentista(Long id) {
        Dentista dentista = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentista", id));
        return toResponse(dentista);
    }

    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public DentistaResponse actualizarDentista(Long id, DentistaRequest request) {
        Dentista dentista = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentista", id));

        dentista.setNombre(request.nombre());
        dentista.setApellido(request.apellido());
        dentista.setTelefono(request.telefono());
        dentista.setEspecialidad(Especialidad.valueOf(request.especialidad()));

        Dentista actualizado = repository.save(dentista);
        return toResponse(actualizado);
    }

    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public void eliminarDentista(Long id) {
        log.info("Iniciando eliminación de dentista con ID: {}", id);
        if (!repository.existsById(id)) {
            log.warn("Intento de eliminar dentista no existente con ID: {}", id);
            throw new ResourceNotFoundException("Dentista", id);
        }
        repository.deleteById(id);
        log.info("Dentista eliminado exitosamente con ID: {}", id);
    }

}
