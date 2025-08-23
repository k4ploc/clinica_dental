package com.clinica.model;

import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.enums.Especialidad;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dentista")
public class Dentista {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String apellido;
	private String telefono;
	@Enumerated(EnumType.STRING)
	private Especialidad especialidad;

	public Dentista(DentistaRequest request) {
		this.nombre = request.getNombre();
		this.apellido = request.getApellido();
		this.telefono = request.getTelefono();
		this.especialidad = Especialidad.from(request.getEspecialidad());

	}

}
