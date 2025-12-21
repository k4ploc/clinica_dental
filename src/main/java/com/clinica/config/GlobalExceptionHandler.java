package com.clinica.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.clinica.errors.DuplicateException;
import com.clinica.errors.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.warn("Excepción de validación detectada: {}", ex.getBindingResult().getFieldError().getField());

		Map<String, Object> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			log.debug("Campo inválido: {} - Razón: {}", error.getField(), error.getDefaultMessage());
			errors.put(error.getField(), error.getDefaultMessage());
		});

		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DuplicateException ex) {
		log.warn("Excepción de duplicado: {}", ex.getMessage());

		Map<String, String> response = new HashMap<>();
		response.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
		log.warn("Recurso no encontrado: {}", ex.getMessage());

		Map<String, String> response = new HashMap<>();
		response.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
		log.error("RuntimeException no controlada: {}", ex.getMessage(), ex);

		Map<String, String> response = new HashMap<>();
		response.put("error", ex.getMessage());

		// Si el mensaje contiene "no encontrado", retornar 404
		if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("no encontrado")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		// Por defecto, retornar 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
