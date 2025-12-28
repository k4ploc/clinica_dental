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

import com.clinica.model.Dentista;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.dto.DentistaResponse;
import com.clinica.model.enums.Especialidad;
import com.clinica.model.enums.EstadoEntidad;
import com.clinica.repository.DentistaRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DentistaServiceTest {

    @Mock
    private DentistaRepository dentistaRepository;

    @InjectMocks
    private DentistaService dentistaService;

    private DentistaRequest dentistaRequest;
    private Dentista dentista;

    @BeforeEach
    void setUp() {
        dentistaRequest = new DentistaRequest(
                "Dr. Juan",
                "Pérez",
                "1234567890",
                "GENERAL"
        );

        dentista = Dentista.builder()
                .id(1L)
                .nombre("Dr. Juan")
                .apellido("Pérez")
                .telefono("1234567890")
                .especialidad(Especialidad.GENERAL)
                .estado(EstadoEntidad.ACTIVO)
                .build();
    }

    @Test
    void testCreateDentista() {
        when(dentistaRepository.save(any(Dentista.class))).thenReturn(dentista);

        Dentista resultado = dentistaService.createDentista(dentistaRequest);

        assertNotNull(resultado);
        assertEquals("Dr. Juan", resultado.getNombre());
        assertEquals(EstadoEntidad.ACTIVO, resultado.getEstado());
        verify(dentistaRepository).save(any(Dentista.class));
    }

    @Test
    void testGetDentistas() {
        when(dentistaRepository.findAllWithPacientes()).thenReturn(List.of(dentista));

        List<DentistaResponse> resultado = dentistaService.getDentistas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Dr. Juan", resultado.get(0).nombre());
        assertEquals(EstadoEntidad.ACTIVO, resultado.get(0).estado());
    }

    @Test
    void testObtenerDentista_Success() {
        when(dentistaRepository.findByIdAndEstadoWithPacientes(1L, EstadoEntidad.ACTIVO)).thenReturn(Optional.of(dentista));

        DentistaResponse resultado = dentistaService.obtenerDentista(1L);

        assertNotNull(resultado);
        assertEquals("Dr. Juan", resultado.nombre());
        assertEquals(1L, resultado.id());
        assertEquals(EstadoEntidad.ACTIVO, resultado.estado());
    }

    @Test
    void testObtenerDentista_NotFound() {
        when(dentistaRepository.findByIdAndEstadoWithPacientes(1L, EstadoEntidad.ACTIVO)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            dentistaService.obtenerDentista(1L);
        });
    }

    @Test
    void testActualizarDentista_Success() {
        when(dentistaRepository.findByIdAndEstadoWithPacientes(1L, EstadoEntidad.ACTIVO)).thenReturn(Optional.of(dentista));
        when(dentistaRepository.save(any(Dentista.class))).thenReturn(dentista);

        DentistaResponse resultado = dentistaService.actualizarDentista(1L, dentistaRequest);

        assertNotNull(resultado);
        assertEquals("Dr. Juan", resultado.nombre());
    }

    @Test
    void testActualizarDentista_NotFound() {
        when(dentistaRepository.findByIdAndEstadoWithPacientes(1L, EstadoEntidad.ACTIVO)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            dentistaService.actualizarDentista(1L, dentistaRequest);
        });
    }

    @Test
    void testEliminarDentista_Success() {
        when(dentistaRepository.findByIdAndEstadoWithPacientes(1L, EstadoEntidad.ACTIVO)).thenReturn(Optional.of(dentista));
        when(dentistaRepository.save(any(Dentista.class))).thenReturn(dentista);

        dentistaService.eliminarDentista(1L);

        verify(dentistaRepository).save(argThat(d -> d.getEstado() == EstadoEntidad.ELIMINADO));
    }

    @Test
    void testEliminarDentista_NotFound() {
        when(dentistaRepository.findByIdAndEstadoWithPacientes(1L, EstadoEntidad.ACTIVO)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            dentistaService.eliminarDentista(1L);
        });
    }
}
