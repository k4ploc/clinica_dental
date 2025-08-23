package com.clinica.model.enums;

public enum Especialidad {
	DENTISTA, CIRUJANO, ORTODONCISTA;

	public static Especialidad from(String especialidad) {

		return Especialidad.valueOf(especialidad.toUpperCase());
	}
}
