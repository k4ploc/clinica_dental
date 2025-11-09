package com.clinica.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PacienteRequest(

		@NotBlank(message = "Nombre requerido") String nombre,
		@NotBlank(message = "Apellido requerido") String apellido,
		@NotBlank(message = "Telefono requerido") String telefono, String email,
		@NotNull(message = "Id dentista requerido") @JsonProperty("id_dentista") Long idDentista) {

}
