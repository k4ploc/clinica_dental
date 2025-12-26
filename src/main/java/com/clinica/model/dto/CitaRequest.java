package com.clinica.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CitaRequest(

    @NotNull(message = "Fecha requerida")
    @Future(message = "La fecha debe ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime fecha,

    String motivo,

    @NotNull(message = "Id paciente requerido")
    @JsonProperty("id_paciente")
    Long idPaciente,

    @NotNull(message = "Id dentista requerido")
    @JsonProperty("id_dentista")
    Long idDentista

) {}
