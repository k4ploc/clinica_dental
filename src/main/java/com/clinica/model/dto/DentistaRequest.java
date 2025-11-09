package com.clinica.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DentistaRequest (

	 String nombre,
	 String apellido,
	 String telefono,

	@NotNull(message = "Especialidad es obligatoria")
	@NotBlank(message = "Especialidad es obligatoria")
	 String especialidad){

}
