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
import com.clinica.model.enums.EstadoEntidad;
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
     * Obtiene dentistas activos paginados (sin cargar pacientes para mejor rendimiento).
     */
    @Transactional(readOnly = true)
    public Page<DentistaResponse> getDentistasPaginados(Pageable pageable) {
        log.debug("Obteniendo dentistas activos paginados: página {}, tamaño {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Dentista> page = repository.findByEstado(EstadoEntidad.ACTIVO, pageable);
        log.info("Se retornan {} dentistas activos de {} total", page.getNumberOfElements(), page.getTotalElements());
        return page.map(this::toResponseSimple);
    }

    /**
     * Convierte Dentista a DentistaResponse CON pacientes (para detalle).
     */
    private DentistaResponse toResponseWithPacientes(Dentista d) {
        List<PacienteResponse> pacientes = Collections.emptyList();
        if (d.getPacientes() != null && !d.getPacientes().isEmpty()) {
            pacientes = d.getPacientes().stream()
                    .filter(p -> p.getEstado() == EstadoEntidad.ACTIVO)
                    .map(p -> new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getTelefono(), p.getEmail(), d.getId(), p.getEstado()))
                    .collect(Collectors.toList());
        }
        String especialidad = d.getEspecialidad() != null ? d.getEspecialidad().name() : null;
        return new DentistaResponse(d.getId(), d.getNombre(), d.getApellido(), d.getTelefono(), especialidad, pacientes, d.getEstado());
    }

    /**
     * Convierte Dentista a DentistaResponse SIN pacientes (para listados).
     */
    private DentistaResponse toResponseSimple(Dentista d) {
        String especialidad = d.getEspecialidad() != null ? d.getEspecialidad().name() : null;
        return new DentistaResponse(d.getId(), d.getNombre(), d.getApellido(), d.getTelefono(), especialidad, null, d.getEstado());
    }

    /**
     * Obtiene un dentista activo por ID con sus pacientes (usa JOIN FETCH).
     */
    @Transactional(readOnly = true)
    public DentistaResponse obtenerDentista(Long id) {
        log.debug("Buscando dentista activo con ID: {} (con pacientes)", id);
        Dentista dentista = repository.findByIdAndEstadoWithPacientes(id, EstadoEntidad.ACTIVO)
                .orElseThrow(() -> {
                    log.warn("Dentista activo no encontrado con ID: {}", id);
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
        Dentista dentista = repository.findByIdAndEstadoWithPacientes(id, EstadoEntidad.ACTIVO)
                .orElseThrow(() -> {
                    log.warn("Dentista activo a actualizar no encontrado con ID: {}", id);
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
        log.info("Iniciando eliminación lógica de dentista con ID: {}", id);
        Dentista dentista = repository.findByIdAndEstadoWithPacientes(id, EstadoEntidad.ACTIVO)
                .orElseThrow(() -> {
                    log.warn("Dentista activo no encontrado para eliminar con ID: {}", id);
                    return new ResourceNotFoundException("Dentista", id);
                });
        dentista.setEstado(EstadoEntidad.ELIMINADO);
        repository.save(dentista);
        log.info("Dentista eliminado lógicamente con ID: {}", id);
    }

    // ==================== MÉTODOS ADMIN ====================

    /**
     * Lista TODOS los dentistas (sin filtro de estado) - Solo admin.
     */
    @Transactional(readOnly = true)
    public Page<DentistaResponse> listarTodosDentistas(Pageable pageable) {
        log.debug("Admin: Obteniendo todos los dentistas paginados");
        Page<Dentista> page = repository.findAll(pageable);
        log.info("Admin: Se retornan {} dentistas de {} total", page.getNumberOfElements(), page.getTotalElements());
        return page.map(this::toResponseSimple);
    }

    /**
     * Lista dentistas por estado específico - Solo admin.
     */
    @Transactional(readOnly = true)
    public Page<DentistaResponse> listarDentistasPorEstado(EstadoEntidad estado, Pageable pageable) {
        log.debug("Admin: Obteniendo dentistas con estado: {}", estado);
        Page<Dentista> page = repository.findByEstado(estado, pageable);
        log.info("Admin: Se retornan {} dentistas con estado {}", page.getNumberOfElements(), estado);
        return page.map(this::toResponseSimple);
    }

    /**
     * Obtiene un dentista por ID sin importar su estado - Solo admin.
     */
    @Transactional(readOnly = true)
    public DentistaResponse obtenerDentistaSinFiltro(Long id) {
        log.debug("Admin: Buscando dentista con ID: {} (sin filtro de estado)", id);
        Dentista dentista = repository.findByIdWithPacientes(id)
                .orElseThrow(() -> {
                    log.warn("Admin: Dentista no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Dentista", id);
                });
        log.info("Admin: Dentista encontrado con estado: {}", dentista.getEstado());
        return toResponseWithPacientesAdmin(dentista);
    }

    /**
     * Cambia el estado de un dentista - Solo admin.
     */
    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public DentistaResponse cambiarEstado(Long id, EstadoEntidad nuevoEstado) {
        log.info("Admin: Cambiando estado de dentista {} a {}", id, nuevoEstado);
        Dentista dentista = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Admin: Dentista no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Dentista", id);
                });
        dentista.setEstado(nuevoEstado);
        Dentista actualizado = repository.save(dentista);
        log.info("Admin: Estado de dentista {} cambiado a {}", id, nuevoEstado);
        return toResponseSimple(actualizado);
    }

    /**
     * Convierte Dentista a DentistaResponse CON todos los pacientes (para admin).
     */
    private DentistaResponse toResponseWithPacientesAdmin(Dentista d) {
        List<PacienteResponse> pacientes = Collections.emptyList();
        if (d.getPacientes() != null && !d.getPacientes().isEmpty()) {
            pacientes = d.getPacientes().stream()
                    .map(p -> new PacienteResponse(p.getId(), p.getNombre(), p.getApellido(), p.getTelefono(), p.getEmail(), d.getId(), p.getEstado()))
                    .collect(Collectors.toList());
        }
        String especialidad = d.getEspecialidad() != null ? d.getEspecialidad().name() : null;
        return new DentistaResponse(d.getId(), d.getNombre(), d.getApellido(), d.getTelefono(), especialidad, pacientes, d.getEstado());
    }

}
