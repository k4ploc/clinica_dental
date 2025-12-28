package com.clinica.controller;

import com.clinica.config.JwtAuthenticationFilter;
import com.clinica.config.JwtService;
import com.clinica.model.Dentista;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.dto.DentistaResponse;
import com.clinica.model.enums.Especialidad;
import com.clinica.service.DentistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DentistaController.class)
@AutoConfigureMockMvc(addFilters = false)
class DentistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DentistaService dentistaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private DentistaRequest dentistaRequest;
    private DentistaResponse dentistaResponse;
    private Dentista dentista;

    @BeforeEach
    void setUp() {
        dentistaRequest = new DentistaRequest(
                "Dr. Juan",
                "Pérez",
                "1234567890",
                "GENERAL"
        );

        dentistaResponse = new DentistaResponse(
                1L,
                "Dr. Juan",
                "Pérez",
                "1234567890",
                "GENERAL",
                null,
                com.clinica.model.enums.EstadoEntidad.ACTIVO
        );

        dentista = Dentista.builder()
                .id(1L)
                .nombre("Dr. Juan")
                .apellido("Pérez")
                .telefono("1234567890")
                .especialidad(Especialidad.GENERAL)
                .estado(com.clinica.model.enums.EstadoEntidad.ACTIVO)
                .build();
    }

    @Test
    void testGetDentistas() throws Exception {
        Page<DentistaResponse> page = new PageImpl<>(List.of(dentistaResponse));
        when(dentistaService.getDentistasPaginados(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/dentista")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre", is("Dr. Juan")));

        verify(dentistaService).getDentistasPaginados(any(Pageable.class));
    }

    @Test
    void testObtenerDentista_Success() throws Exception {
        when(dentistaService.obtenerDentista(1L)).thenReturn(dentistaResponse);

        mockMvc.perform(get("/api/dentista/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Dr. Juan")));

        verify(dentistaService).obtenerDentista(1L);
    }

    @Test
    void testObtenerDentista_NotFound() throws Exception {
        when(dentistaService.obtenerDentista(1L))
                .thenThrow(new RuntimeException("Dentista no encontrado"));

        mockMvc.perform(get("/api/dentista/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearDentista_Success() throws Exception {
        when(dentistaService.createDentista(any(DentistaRequest.class)))
                .thenReturn(dentista);

        mockMvc.perform(post("/api/dentista")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dentistaRequest)))
                .andExpect(status().isCreated());

        verify(dentistaService).createDentista(any(DentistaRequest.class));
    }

    @Test
    void testCrearDentista_ValidationError() throws Exception {
        DentistaRequest invalidRequest = new DentistaRequest(
                "Dr. Juan",
                "Pérez",
                "1234567890",
                "GENERAL"
        );

        when(dentistaService.createDentista(any(DentistaRequest.class)))
                .thenReturn(dentista);

        mockMvc.perform(post("/api/dentista")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void testActualizarDentista_Success() throws Exception {
        when(dentistaService.actualizarDentista(anyLong(), any(DentistaRequest.class)))
                .thenReturn(dentistaResponse);

        mockMvc.perform(put("/api/dentista/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dentistaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Dr. Juan")));

        verify(dentistaService).actualizarDentista(anyLong(), any(DentistaRequest.class));
    }

    @Test
    void testEliminarDentista_Success() throws Exception {
        doNothing().when(dentistaService).eliminarDentista(anyLong());

        mockMvc.perform(delete("/api/dentista/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(dentistaService).eliminarDentista(1L);
    }

    @Test
    void testEliminarDentista_NotFound() throws Exception {
        doThrow(new RuntimeException("Dentista no encontrado"))
                .when(dentistaService).eliminarDentista(anyLong());

        mockMvc.perform(delete("/api/dentista/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
