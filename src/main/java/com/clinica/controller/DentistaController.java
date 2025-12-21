package com.clinica.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private static final Logger log = LoggerFactory.getLogger(DentistaController.class);

    private final DentistaService service;

    public DentistaController(DentistaService service) {
        this.service = service;
        log.info("DentistaController inicializado");
    }

    @GetMapping
    public ResponseEntity<Page<DentistaResponse>> getDentistas(Pageable pageable) {
        log.debug("GET /dentista - Listando dentistas con paginación");
        Page<DentistaResponse> page = service.getDentistasPaginados(pageable);
        if (page != null) {
            log.info("GET /dentista - {} dentistas retornados", page.getNumberOfElements());
        }
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DentistaResponse> obtenerDentista(@PathVariable Long id) {
        log.debug("GET /dentista/{} - Obteniendo dentista específico", id);
        DentistaResponse response = service.obtenerDentista(id);
        log.info("GET /dentista/{} - Dentista obtenido exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DentistaResponse> createDentista(@Valid @RequestBody DentistaRequest request) {
        log.debug("POST /dentista - Creando nuevo dentista: {} {}", request.nombre(), request.apellido());
        Dentista newDentista = service.createDentista(request);
        DentistaResponse response = new DentistaResponse(newDentista.getId(), newDentista.getNombre(), newDentista.getApellido(), newDentista.getTelefono(), newDentista.getEspecialidad() != null ? newDentista.getEspecialidad().name() : null, null);
        log.info("POST /dentista - Dentista creado exitosamente con ID: {}", newDentista.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DentistaResponse> actualizarDentista(
            @PathVariable Long id,
            @Valid @RequestBody DentistaRequest request) {
        log.debug("PUT /dentista/{} - Actualizando dentista", id);
        DentistaResponse response = service.actualizarDentista(id, request);
        log.info("PUT /dentista/{} - Dentista actualizado exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDentista(@PathVariable Long id) {
        log.debug("DELETE /dentista/{} - Eliminando dentista", id);
        service.eliminarDentista(id);
        log.info("DELETE /dentista/{} - Dentista eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }

}
