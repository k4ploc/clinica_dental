package com.clinica.model.dto;

import com.clinica.model.enums.EstadoEntidad;

public record PacienteResponse(
    Long id,
    String nombre,
    String apellido,
    String telefono,
    String email,
    Long idDentista,
    EstadoEntidad estado
) {}

