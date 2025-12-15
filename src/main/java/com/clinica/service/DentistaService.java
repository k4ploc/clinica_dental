package com.clinica.service;

import com.clinica.model.dto.DentistaRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    private final DentistaRepository repository;

    public DentistaService(DentistaRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(value = "dentistas", allEntries = true)
    public Dentista createDentista(DentistaRequest request) {
        return repository.save(new Dentista(request));
    }

    @Cacheable(value = "dentistas")
    public List<DentistaResponse> getDentistas() {
        List<Dentista> lista = repository.findAll();
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
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

    public DentistaResponse obtenerDentista(Long id) {
        Dentista dentista = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentista no encontrado con ID: " + id));
        return toResponse(dentista);
    }

    @CacheEvict(value = "dentistas", allEntries = true)
    public DentistaResponse actualizarDentista(Long id, DentistaRequest request) {
        Dentista dentista = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentista no encontrado con ID: " + id));

        dentista.setNombre(request.nombre());
        dentista.setApellido(request.apellido());
        dentista.setTelefono(request.telefono());
        dentista.setEspecialidad(Especialidad.valueOf(request.especialidad()));

        Dentista actualizado = repository.save(dentista);
        return toResponse(actualizado);
    }

    @CacheEvict(value = "dentistas", allEntries = true)
    public void eliminarDentista(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Dentista no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

}
