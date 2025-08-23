package com.clinica.service;

import org.springframework.stereotype.Service;

import com.clinica.model.Dentista;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.repository.DentistaRepository;

@Service
public class DentistaService {

	private final DentistaRepository repository;

	public DentistaService(DentistaRepository repository) {
		this.repository = repository;
	}

	public Dentista createDentista(DentistaRequest request) {
		return repository.save(new Dentista(request));
	}

}
