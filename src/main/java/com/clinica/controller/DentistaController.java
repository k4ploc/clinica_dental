package com.clinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinica.model.Dentista;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.dto.DentistaResponse;
import com.clinica.service.DentistaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/dentista")
public class DentistaController {

    private final DentistaService service;

    public DentistaController(DentistaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getDentistas() {
        var listaDentistas = service.getDentistas();
        return ResponseEntity.ok(listaDentistas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DentistaResponse> obtenerDentista(@PathVariable Long id) {
        DentistaResponse response = service.obtenerDentista(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DentistaResponse> createDentista(@Valid @RequestBody DentistaRequest request) {
        Dentista newDentista = service.createDentista(request);
        // map to DTO
        DentistaResponse response = new DentistaResponse(newDentista.getId(), newDentista.getNombre(), newDentista.getApellido(), newDentista.getTelefono(), newDentista.getEspecialidad() != null ? newDentista.getEspecialidad().name() : null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DentistaResponse> actualizarDentista(
            @PathVariable Long id,
            @Valid @RequestBody DentistaRequest request) {
        DentistaResponse response = service.actualizarDentista(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDentista(@PathVariable Long id) {
        service.eliminarDentista(id);
        return ResponseEntity.noContent().build();
    }

}
