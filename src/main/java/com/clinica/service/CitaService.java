package com.clinica.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinica.errors.ResourceNotFoundException;
import com.clinica.model.Cita;
import com.clinica.model.Dentista;
import com.clinica.model.Paciente;
import com.clinica.model.dto.CitaRequest;
import com.clinica.model.dto.CitaResponse;
import com.clinica.model.enums.EstadoCita;
import com.clinica.repository.CitaRepository;
import com.clinica.repository.DentistaRepository;
import com.clinica.repository.PacienteRepository;

@Service
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;

    public CitaService(CitaRepository citaRepository,
                       PacienteRepository pacienteRepository,
                       DentistaRepository dentistaRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
        log.info("CitaService inicializado correctamente");
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "citas")
    public List<CitaResponse> listarCitas() {
        log.debug("Obteniendo lista completa de citas activas");
        var lista = citaRepository.findAllActiveWithRelations(EstadoCita.CANCELADA);
        log.info("Se encontraron {} citas activas", lista.size());
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CitaResponse> listarCitasPaginadas(Pageable pageable) {
        log.debug("Obteniendo citas paginadas: pagina {}, tamanio {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Cita> page = citaRepository.findAllByEstadoNot(EstadoCita.CANCELADA, pageable);
        log.info("Se retornan {} citas de la pagina {} (total: {} registros)",
                page.getNumberOfElements(), page.getNumber(), page.getTotalElements());
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CitaResponse obtenerCita(Long id) {
        log.debug("Buscando cita con ID: {}", id);
        Cita cita = citaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> {
                    log.warn("Cita no encontrada con ID: {}", id);
                    return new ResourceNotFoundException("Cita", id);
                });
        log.info("Cita encontrada: ID {} para paciente {} (fecha: {})",
                id, cita.getPaciente().getNombre(), cita.getFecha());
        return toResponse(cita);
    }

    @Transactional
    @CacheEvict(value = "citas", allEntries = true)
    public CitaResponse crearCita(CitaRequest request) {
        log.info("Iniciando creacion de nueva cita para fecha: {}", request.fecha());

        // Validar que el paciente existe
        log.debug("Buscando paciente con ID: {}", request.idPaciente());
        Paciente paciente = pacienteRepository.findById(request.idPaciente())
                .orElseThrow(() -> {
                    log.error("Paciente no encontrado con ID: {}", request.idPaciente());
                    return new ResourceNotFoundException("Paciente", request.idPaciente());
                });

        // Validar que el dentista existe
        log.debug("Buscando dentista con ID: {}", request.idDentista());
        Dentista dentista = dentistaRepository.findById(request.idDentista())
                .orElseThrow(() -> {
                    log.error("Dentista no encontrado con ID: {}", request.idDentista());
                    return new ResourceNotFoundException("Dentista", request.idDentista());
                });

        // Verificar disponibilidad del dentista
        if (citaRepository.existsByDentistaIdAndFecha(request.idDentista(), request.fecha())) {
            log.warn("El dentista {} ya tiene una cita programada para {}",
                    dentista.getNombre(), request.fecha());
            throw new IllegalStateException("El dentista ya tiene una cita programada para esa fecha y hora");
        }

        Cita cita = Cita.builder()
                .fecha(request.fecha())
                .motivo(request.motivo())
                .paciente(paciente)
                .dentista(dentista)
                .build();

        Cita saved = citaRepository.save(cita);
        log.info("Cita creada exitosamente con ID: {} para paciente: {} con dentista: {}",
                saved.getId(), paciente.getNombre(), dentista.getNombre());
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "citas", allEntries = true)
    public CitaResponse actualizarCita(Long id, CitaRequest request) {
        log.info("Iniciando actualizacion de cita con ID: {}", id);

        Cita cita = citaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> {
                    log.warn("Cita a actualizar no encontrada con ID: {}", id);
                    return new ResourceNotFoundException("Cita", id);
                });

        // Validar paciente si cambio
        Paciente paciente = cita.getPaciente();
        if (!cita.getPaciente().getId().equals(request.idPaciente())) {
            log.debug("Cambiando paciente de {} a {}", cita.getPaciente().getId(), request.idPaciente());
            paciente = pacienteRepository.findById(request.idPaciente())
                    .orElseThrow(() -> {
                        log.error("Nuevo paciente no encontrado con ID: {}", request.idPaciente());
                        return new ResourceNotFoundException("Paciente", request.idPaciente());
                    });
        }

        // Validar dentista si cambio
        Dentista dentista = cita.getDentista();
        if (!cita.getDentista().getId().equals(request.idDentista())) {
            log.debug("Cambiando dentista de {} a {}", cita.getDentista().getId(), request.idDentista());
            dentista = dentistaRepository.findById(request.idDentista())
                    .orElseThrow(() -> {
                        log.error("Nuevo dentista no encontrado con ID: {}", request.idDentista());
                        return new ResourceNotFoundException("Dentista", request.idDentista());
                    });
        }

        // Verificar disponibilidad si cambio la fecha o el dentista
        if (!cita.getFecha().equals(request.fecha()) || !cita.getDentista().getId().equals(request.idDentista())) {
            if (citaRepository.existsByDentistaIdAndFecha(request.idDentista(), request.fecha())) {
                log.warn("El dentista {} ya tiene una cita programada para {}",
                        dentista.getNombre(), request.fecha());
                throw new IllegalStateException("El dentista ya tiene una cita programada para esa fecha y hora");
            }
        }

        cita.setFecha(request.fecha());
        cita.setMotivo(request.motivo());
        cita.setPaciente(paciente);
        cita.setDentista(dentista);

        Cita actualizada = citaRepository.save(cita);
        log.info("Cita actualizada exitosamente: ID {} (fecha: {})", id, actualizada.getFecha());
        return toResponse(actualizada);
    }

    @Transactional
    @CacheEvict(value = "citas", allEntries = true)
    public void eliminarCita(Long id) {
        log.info("Iniciando cancelacion (baja logica) de cita con ID: {}", id);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de cancelar cita no existente con ID: {}", id);
                    return new ResourceNotFoundException("Cita", id);
                });

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            log.warn("La cita con ID: {} ya esta cancelada", id);
            throw new IllegalStateException("La cita ya esta cancelada");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
        log.info("Cita cancelada exitosamente (baja logica) con ID: {}", id);
    }

    @Transactional
    @CacheEvict(value = "citas", allEntries = true)
    public CitaResponse cambiarEstado(Long id, EstadoCita nuevoEstado) {
        log.info("Cambiando estado de cita ID: {} a {}", id, nuevoEstado);
        Cita cita = citaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> {
                    log.warn("Cita no encontrada con ID: {}", id);
                    return new ResourceNotFoundException("Cita", id);
                });

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            log.warn("No se puede cambiar estado de cita cancelada ID: {}", id);
            throw new IllegalStateException("No se puede modificar una cita cancelada");
        }

        cita.setEstado(nuevoEstado);
        Cita actualizada = citaRepository.save(cita);
        log.info("Estado de cita ID: {} cambiado a {}", id, nuevoEstado);
        return toResponse(actualizada);
    }

    // === Metodos adicionales de consulta ===

    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasPorPaciente(Long pacienteId) {
        log.debug("Buscando citas del paciente con ID: {}", pacienteId);
        List<Cita> citas = citaRepository.findByPacienteId(pacienteId);
        log.info("Se encontraron {} citas para el paciente ID: {}", citas.size(), pacienteId);
        return citas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasPorDentista(Long dentistaId) {
        log.debug("Buscando citas del dentista con ID: {}", dentistaId);
        List<Cita> citas = citaRepository.findByDentistaId(dentistaId);
        log.info("Se encontraron {} citas para el dentista ID: {}", citas.size(), dentistaId);
        return citas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Buscando citas entre {} y {}", inicio, fin);
        List<Cita> citas = citaRepository.findByFechaBetween(inicio, fin);
        log.info("Se encontraron {} citas en el rango de fechas", citas.size());
        return citas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasFuturasPaciente(Long pacienteId) {
        log.debug("Buscando citas futuras del paciente con ID: {}", pacienteId);
        List<Cita> citas = citaRepository.findCitasFuturasByPacienteId(pacienteId, LocalDateTime.now());
        log.info("Se encontraron {} citas futuras para el paciente ID: {}", citas.size(), pacienteId);
        return citas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ==================== MÉTODOS ADMIN ====================

    /**
     * Lista TODAS las citas (sin filtro de estado) - Solo admin.
     */
    @Transactional(readOnly = true)
    public Page<CitaResponse> listarTodasCitas(Pageable pageable) {
        log.debug("Admin: Obteniendo todas las citas paginadas");
        Page<Cita> page = citaRepository.findAll(pageable);
        log.info("Admin: Se retornan {} citas de {} total", page.getNumberOfElements(), page.getTotalElements());
        return page.map(this::toResponse);
    }

    /**
     * Lista citas por estado específico - Solo admin.
     */
    @Transactional(readOnly = true)
    public Page<CitaResponse> listarCitasPorEstado(EstadoCita estado, Pageable pageable) {
        log.debug("Admin: Obteniendo citas con estado: {}", estado);
        Page<Cita> page = citaRepository.findByEstado(estado, pageable);
        log.info("Admin: Se retornan {} citas con estado {}", page.getNumberOfElements(), estado);
        return page.map(this::toResponse);
    }

    /**
     * Cambia el estado de una cita (permite reactivar canceladas) - Solo admin.
     */
    @Transactional
    @CacheEvict(value = "citas", allEntries = true)
    public CitaResponse cambiarEstadoAdmin(Long id, EstadoCita nuevoEstado) {
        log.info("Admin: Cambiando estado de cita {} a {}", id, nuevoEstado);
        Cita cita = citaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> {
                    log.warn("Admin: Cita no encontrada con ID: {}", id);
                    return new ResourceNotFoundException("Cita", id);
                });

        EstadoCita estadoAnterior = cita.getEstado();
        cita.setEstado(nuevoEstado);
        Cita actualizada = citaRepository.save(cita);
        log.info("Admin: Estado de cita {} cambiado de {} a {}", id, estadoAnterior, nuevoEstado);
        return toResponse(actualizada);
    }

    // === Mapper ===

    private CitaResponse toResponse(Cita cita) {
        if (cita == null) return null;

        CitaResponse.PacienteInfo pacienteInfo = null;
        if (cita.getPaciente() != null) {
            pacienteInfo = new CitaResponse.PacienteInfo(
                cita.getPaciente().getId(),
                cita.getPaciente().getNombre(),
                cita.getPaciente().getApellido(),
                cita.getPaciente().getTelefono()
            );
        }

        CitaResponse.DentistaInfo dentistaInfo = null;
        if (cita.getDentista() != null) {
            dentistaInfo = new CitaResponse.DentistaInfo(
                cita.getDentista().getId(),
                cita.getDentista().getNombre(),
                cita.getDentista().getApellido(),
                cita.getDentista().getEspecialidad() != null ?
                    cita.getDentista().getEspecialidad().name() : null
            );
        }

        return new CitaResponse(
            cita.getId(),
            cita.getFecha(),
            cita.getMotivo(),
            cita.getEstado() != null ? cita.getEstado().name() : null,
            pacienteInfo,
            dentistaInfo,
            cita.getCreatedAt(),
            cita.getUpdatedAt()
        );
    }
}
