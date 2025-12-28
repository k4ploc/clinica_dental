package com.clinica.model.enums;

public enum Especialidad {
	ORTODONCIA, ENDODONCIA, PERIODONCIA,CIRUGIA,ODONTOPEDIATRIA,GENERAL;

	public static Especialidad from(String especialidad) {

		return Especialidad.valueOf(especialidad.toUpperCase());
	}
}
