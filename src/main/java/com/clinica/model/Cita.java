package com.clinica.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cita {
	private String id;
	private String pacienteId;
	private String dentistaId;
	private LocalDateTime fechaHora;
	private String motivo;
}