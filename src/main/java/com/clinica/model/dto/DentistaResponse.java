package com.clinica.model.dto;

import java.util.List;

public record DentistaResponse(
    Long id,
    String nombre,
    String apellido,
    String telefono,
    String especialidad,
    List<PacienteResponse> pacientes
) {}

