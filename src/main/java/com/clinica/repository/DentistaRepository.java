package com.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinica.model.Dentista;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {

}
