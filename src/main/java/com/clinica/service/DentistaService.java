package com.clinica.service;

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
import com.clinica.model.dto.DentistaResponse;
import com.clinica.model.dto.PacienteResponse;
import com.clinica.model.enums.Especialidad;
import com.clinica.repository.DentistaRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DentistaService {

    private static final Logger log = LoggerFactory.getLogger(DentistaService.class);

    private final DentistaRepository repository;

    public DentistaService(DentistaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public Dentista createDentista(DentistaRequest request) {
        return repository.save(new Dentista(request));
    }

    /**
     * Obtiene todos los dentistas con sus pacientes (usa JOIN FETCH para evitar N+1).
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "dentistas")
    public List<DentistaResponse> getDentistas() {
        log.debug("Obteniendo lista de dentistas con pacientes (JOIN FETCH)");
        List<Dentista> lista = repository.findAllWithPacientes();
        log.info("Se encontraron {} dentistas", lista.size());
        return lista.stream().map(this::toResponseWithPacientes).collect(Collectors.toList());
    }

    /**
     * Obtiene dentistas paginados (sin cargar pacientes para mejor rendimiento).
     */
    @Transactional(readOnly = true)
    public Page<DentistaResponse> getDentistasPaginados(Pageable pageable) {
        log.debug("Obteniendo dentistas paginados: página {}, tamaño {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Dentista> page = repository.findAll(pageable);
        log.info("Se retornan {} dentistas de {} total", page.getNumberOfElements(), page.getTotalElements());
        return page.map(this::toResponseSimple);
    }

    /**
     * Convierte Dentista a DentistaResponse CON pacientes (para detalle).
     */
    private DentistaResponse toResponseWithPacientes(Dentista d) {
        List<PacienteResponse> pacientes = Collections.emptyList();
        if (d.getPacientes() != null && !d.getPacientes().isEmpty()) {
            pacientes = d.getPacientes().stream()
                    .map(p -> new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getTelefono(), p.getEmail()))
                    .collect(Collectors.toList());
        }
        String especialidad = d.getEspecialidad() != null ? d.getEspecialidad().name() : null;
        return new DentistaResponse(d.getId(), d.getNombre(), d.getApellido(), d.getTelefono(), especialidad, pacientes);
    }

    /**
     * Convierte Dentista a DentistaResponse SIN pacientes (para listados).
     */
    private DentistaResponse toResponseSimple(Dentista d) {
        String especialidad = d.getEspecialidad() != null ? d.getEspecialidad().name() : null;
        return new DentistaResponse(d.getId(), d.getNombre(), d.getApellido(), d.getTelefono(), especialidad, null);
    }

    /**
     * Obtiene un dentista por ID con sus pacientes (usa JOIN FETCH).
     */
    @Transactional(readOnly = true)
    public DentistaResponse obtenerDentista(Long id) {
        log.debug("Buscando dentista con ID: {} (con pacientes)", id);
        Dentista dentista = repository.findByIdWithPacientes(id)
                .orElseThrow(() -> {
                    log.warn("Dentista no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Dentista", id);
                });
        log.info("Dentista encontrado: {} {} con {} pacientes",
                dentista.getNombre(), dentista.getApellido(),
                dentista.getPacientes() != null ? dentista.getPacientes().size() : 0);
        return toResponseWithPacientes(dentista);
    }

    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public DentistaResponse actualizarDentista(Long id, DentistaRequest request) {
        log.info("Iniciando actualización de dentista con ID: {}", id);
        Dentista dentista = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Dentista a actualizar no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Dentista", id);
                });

        log.debug("Actualizando datos: {} {} -> {} {}",
                dentista.getNombre(), dentista.getApellido(),
                request.nombre(), request.apellido());

        dentista.setNombre(request.nombre());
        dentista.setApellido(request.apellido());
        dentista.setTelefono(request.telefono());
        dentista.setEspecialidad(Especialidad.valueOf(request.especialidad()));

        Dentista actualizado = repository.save(dentista);
        log.info("Dentista actualizado exitosamente con ID: {}", id);
        return toResponseSimple(actualizado);
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
