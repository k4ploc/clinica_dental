package com.clinica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinica.model.Cita;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Obtiene una cita con paciente y dentista en una sola query.
     */
    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.paciente LEFT JOIN FETCH c.dentista WHERE c.id = :id")
    Optional<Cita> findByIdWithRelations(@Param("id") Long id);

    /**
     * Obtiene todas las citas con sus relaciones.
     */
    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.paciente LEFT JOIN FETCH c.dentista")
    List<Cita> findAllWithRelations();

    /**
     * Busca citas por paciente ID.
     */
    List<Cita> findByPacienteId(Long pacienteId);

    /**
     * Busca citas por paciente ID con paginacion.
     */
    Page<Cita> findByPacienteId(Long pacienteId, Pageable pageable);

    /**
     * Busca citas por dentista ID.
     */
    List<Cita> findByDentistaId(Long dentistaId);

    /**
     * Busca citas por dentista ID con paginacion.
     */
    Page<Cita> findByDentistaId(Long dentistaId, Pageable pageable);

    /**
     * Busca citas en un rango de fechas.
     */
    @Query("SELECT c FROM Cita c WHERE c.fecha BETWEEN :inicio AND :fin ORDER BY c.fecha ASC")
    List<Cita> findByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    /**
     * Busca citas de un dentista en un rango de fechas.
     */
    @Query("SELECT c FROM Cita c WHERE c.dentista.id = :dentistaId AND c.fecha BETWEEN :inicio AND :fin ORDER BY c.fecha ASC")
    List<Cita> findByDentistaIdAndFechaBetween(
        @Param("dentistaId") Long dentistaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Busca citas de un paciente en un rango de fechas.
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId AND c.fecha BETWEEN :inicio AND :fin ORDER BY c.fecha ASC")
    List<Cita> findByPacienteIdAndFechaBetween(
        @Param("pacienteId") Long pacienteId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Verifica si existe una cita para un dentista en una fecha especifica.
     */
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.dentista.id = :dentistaId AND c.fecha = :fecha")
    boolean existsByDentistaIdAndFecha(@Param("dentistaId") Long dentistaId, @Param("fecha") LocalDateTime fecha);

    /**
     * Cuenta citas por dentista.
     */
    long countByDentistaId(Long dentistaId);

    /**
     * Cuenta citas por paciente.
     */
    long countByPacienteId(Long pacienteId);

    /**
     * Busca citas futuras de un paciente.
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId AND c.fecha > :ahora ORDER BY c.fecha ASC")
    List<Cita> findCitasFuturasByPacienteId(@Param("pacienteId") Long pacienteId, @Param("ahora") LocalDateTime ahora);

    /**
     * Busca citas del dia para un dentista.
     */
    @Query("SELECT c FROM Cita c WHERE c.dentista.id = :dentistaId AND DATE(c.fecha) = DATE(:fecha) ORDER BY c.fecha ASC")
    List<Cita> findCitasDelDiaByDentistaId(@Param("dentistaId") Long dentistaId, @Param("fecha") LocalDateTime fecha);
}
