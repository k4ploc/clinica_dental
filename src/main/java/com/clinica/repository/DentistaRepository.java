package com.clinica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinica.model.Dentista;
import com.clinica.model.enums.Especialidad;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {

    /**
     * Obtiene un dentista con sus pacientes en una sola query (evita N+1).
     */
    @Query("SELECT DISTINCT d FROM Dentista d LEFT JOIN FETCH d.pacientes WHERE d.id = :id")
    Optional<Dentista> findByIdWithPacientes(@Param("id") Long id);

    /**
     * Obtiene todos los dentistas con sus pacientes en una sola query (evita N+1).
     */
    @Query("SELECT DISTINCT d FROM Dentista d LEFT JOIN FETCH d.pacientes")
    List<Dentista> findAllWithPacientes();

    /**
     * Busca dentistas por especialidad.
     */
    List<Dentista> findByEspecialidad(Especialidad especialidad);

    /**
     * Busca dentistas por nombre (case insensitive).
     */
    @Query("SELECT d FROM Dentista d WHERE LOWER(d.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Dentista> findByNombreContaining(@Param("nombre") String nombre);

    /**
     * Busca dentistas por apellido (case insensitive).
     */
    @Query("SELECT d FROM Dentista d WHERE LOWER(d.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))")
    List<Dentista> findByApellidoContaining(@Param("apellido") String apellido);

    /**
     * Cuenta pacientes por dentista (útil para reportes).
     */
    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.dentista.id = :dentistaId")
    long countPacientesByDentistaId(@Param("dentistaId") Long dentistaId);

    /**
     * Verifica si existe un dentista con el teléfono dado.
     */
    boolean existsByTelefono(String telefono);
}
