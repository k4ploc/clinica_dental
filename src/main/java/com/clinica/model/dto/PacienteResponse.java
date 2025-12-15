package com.clinica.model.dto;

public record PacienteResponse(
    Long id,
    String nombre,
    String apellido,
    String telefono,
    String email
) {}

