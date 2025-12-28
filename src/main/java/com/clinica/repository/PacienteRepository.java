package com.clinica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinica.model.Paciente;
import com.clinica.model.enums.EstadoEntidad;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByEmail(String email);

    /**
     * Obtiene un paciente con su dentista en una sola query (evita lazy loading).
     */
    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.dentista WHERE p.id = :id")
    Optional<Paciente> findByIdWithDentista(@Param("id") Long id);

    /**
     * Obtiene todos los pacientes con su dentista en una sola query.
     */
    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.dentista")
    List<Paciente> findAllWithDentista();

    /**
     * Busca pacientes por dentista ID.
     */
    List<Paciente> findByDentistaId(Long dentistaId);

    /**
     * Busca pacientes por dentista ID con paginación.
     */
    Page<Paciente> findByDentistaId(Long dentistaId, Pageable pageable);

    /**
     * Busca pacientes por nombre (case insensitive).
     */
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Paciente> findByNombreContaining(@Param("nombre") String nombre);

    /**
     * Busca pacientes por email (case insensitive).
     */
    Optional<Paciente> findByEmailIgnoreCase(String email);

    /**
     * Cuenta pacientes por dentista.
     */
    long countByDentistaId(Long dentistaId);

    /**
     * Obtiene pacientes activos con paginación.
     */
    Page<Paciente> findByEstado(EstadoEntidad estado, Pageable pageable);

    /**
     * Obtiene un paciente activo por ID.
     */
    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.dentista WHERE p.id = :id AND p.estado = :estado")
    Optional<Paciente> findByIdAndEstado(@Param("id") Long id, @Param("estado") EstadoEntidad estado);

    /**
     * Busca pacientes activos por dentista ID.
     */
    List<Paciente> findByDentistaIdAndEstado(Long dentistaId, EstadoEntidad estado);

    /**
     * Cuenta pacientes activos por dentista.
     */
    long countByDentistaIdAndEstado(Long dentistaId, EstadoEntidad estado);
}
