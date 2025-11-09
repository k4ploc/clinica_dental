package com.clinica.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.model.Dentista;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.service.DentistaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/dentista")
public class DentistaController {

	private final DentistaService service;

	public DentistaController(DentistaService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Dentista> createDentista(@Valid @RequestBody DentistaRequest request) {
		Dentista newDentista = service.createDentista(request);
		return ResponseEntity.ok(newDentista);
	}

}
