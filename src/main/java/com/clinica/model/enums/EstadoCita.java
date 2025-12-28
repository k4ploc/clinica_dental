package com.clinica.model.enums;

public enum EstadoCita {
	PROGRAMADA,
	CONFIRMADA,
	EN_CURSO,
	COMPLETADA,
	CANCELADA;

	public static EstadoCita from(String estado) {
		return EstadoCita.valueOf(estado.toUpperCase());
	}
}
