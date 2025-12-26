package com.clinica.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CitaResponse(
    Long id,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime fecha,

    String motivo,

    PacienteInfo paciente,

    DentistaInfo dentista,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt
) {

    /**
     * Informacion resumida del paciente para la cita.
     */
    public record PacienteInfo(
        Long id,
        String nombre,
        String apellido,
        String telefono
    ) {}

    /**
     * Informacion resumida del dentista para la cita.
     */
    public record DentistaInfo(
        Long id,
        String nombre,
        String apellido,
        String especialidad
    ) {}
}
