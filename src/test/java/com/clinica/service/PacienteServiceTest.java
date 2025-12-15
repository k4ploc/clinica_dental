package com.clinica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinica.errors.DuplicateException;
import com.clinica.model.Dentista;
import com.clinica.model.Paciente;
import com.clinica.model.dto.PacienteRequest;
import com.clinica.model.dto.PacienteResponse;
import com.clinica.repository.DentistaRepository;
import com.clinica.repository.PacienteRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private DentistaRepository dentistaRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private PacienteRequest pacienteRequest;
    private Dentista dentista;
    private Paciente paciente;

    @BeforeEach
    void setUp() {
        dentista = Dentista.builder()
                .id(1L)
                .nombre("Dr. Juan")
                .apellido("Pérez")
                .telefono("1234567890")
                .build();

        pacienteRequest = new PacienteRequest(
                "Carlos",
                "López",
                "9876543210",
                "carlos@example.com",
                1L
        );

        paciente = Paciente.builder()
                .id(1L)
                .nombre("Carlos")
                .apellido("López")
                .telefono("9876543210")
                .email("carlos@example.com")
                .dentista(dentista)
                .build();
    }

    @Test
    void testCrearPaciente_Success() {
        when(pacienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        PacienteResponse response = pacienteService.crearPaciente(pacienteRequest);

        assertNotNull(response);
        assertEquals("Carlos", response.nombre());
        assertEquals("carlos@example.com", response.email());
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    void testCrearPaciente_DuplicateEmail() {
        when(pacienteRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateException.class, () -> {
            pacienteService.crearPaciente(pacienteRequest);
        });
    }

    @Test
    void testObtenerPaciente_Success() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        PacienteResponse response = pacienteService.obtenerPaciente(1L);

        assertNotNull(response);
        assertEquals("Carlos", response.nombre());
        assertEquals(1L, response.id());
    }

    @Test
    void testObtenerPaciente_NotFound() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pacienteService.obtenerPaciente(1L);
        });
    }

    @Test
    void testActualizarPaciente_Success() {
        PacienteRequest requestConEmailNuevo = new PacienteRequest(
                "Carlos",
                "López",
                "9876543210",
                "newemail@example.com",
                1L
        );

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        PacienteResponse response = pacienteService.actualizarPaciente(1L, requestConEmailNuevo);

        assertNotNull(response);
        assertEquals("Carlos", response.nombre());
    }

    @Test
    void testActualizarPaciente_NotFound() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pacienteService.actualizarPaciente(1L, pacienteRequest);
        });
    }

    @Test
    void testEliminarPaciente_Success() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);

        pacienteService.eliminarPaciente(1L);

        verify(pacienteRepository).deleteById(1L);
    }

    @Test
    void testEliminarPaciente_NotFound() {
        when(pacienteRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            pacienteService.eliminarPaciente(1L);
        });
    }

    @Test
    void testListarPacientes() {
        when(pacienteRepository.findAll()).thenReturn(java.util.List.of(paciente));

        var response = pacienteService.listarPacientes();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Carlos", response.get(0).nombre());
    }
}

