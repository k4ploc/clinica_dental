package com.clinica.model;

import java.util.List;

import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.enums.Especialidad;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	@OneToMany(mappedBy = "dentista", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Paciente> pacientes;

	public Dentista(DentistaRequest request) {
		this.nombre = request.nombre();
		this.apellido = request.apellido();
		this.telefono = request.telefono();
		this.especialidad = Especialidad.from(request.especialidad());

	}

}
