package com.clinica.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {
	private String id;
	private String nombre;
	private String apellido;
	private String telefono;
	private String email;
}
