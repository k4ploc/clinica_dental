package com.clinica.model;

import com.clinica.model.dto.PacienteRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paciente")
public class Paciente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String nombre;
	@NotBlank
	private String apellido;
	@NotBlank
	private String telefono;
	private String email;
	@ManyToOne
	@JoinColumn(name = "dentista_id")
	private Dentista dentista;

	public Paciente(PacienteRequest request, Dentista dentista) {
		this.nombre = request.nombre();
		this.apellido = request.apellido();
		this.telefono = request.telefono();
		this.email = request.email();
		this.dentista = dentista;
	}

}
