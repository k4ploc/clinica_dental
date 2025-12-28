package com.clinica.controller;

import com.clinica.config.JwtAuthenticationFilter;
import com.clinica.config.JwtService;
import com.clinica.model.dto.PacienteRequest;
import com.clinica.model.dto.PacienteResponse;
import com.clinica.service.PacienteService;
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

@WebMvcTest(PacienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PacienteRequest pacienteRequest;
    private PacienteResponse pacienteResponse;

    @BeforeEach
    void setUp() {
        pacienteRequest = new PacienteRequest(
                "Carlos",
                "López",
                "9876543210",
                "carlos@example.com",
                1L
        );

        pacienteResponse = new PacienteResponse(
                1L,
                "Carlos",
                "López",
                "9876543210",
                "carlos@example.com",
                1L,
                com.clinica.model.enums.EstadoEntidad.ACTIVO
        );
    }

    @Test
    void testListarPacientes() throws Exception {
        Page<PacienteResponse> page = new PageImpl<>(List.of(pacienteResponse));
        when(pacienteService.listarPacientesPaginados(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre", is("Carlos")));

        verify(pacienteService).listarPacientesPaginados(any(Pageable.class));
    }

    @Test
    void testObtenerPaciente_Success() throws Exception {
        when(pacienteService.obtenerPaciente(1L)).thenReturn(pacienteResponse);

        mockMvc.perform(get("/api/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Carlos")));

        verify(pacienteService).obtenerPaciente(1L);
    }

    @Test
    void testObtenerPaciente_NotFound() throws Exception {
        when(pacienteService.obtenerPaciente(1L))
                .thenThrow(new RuntimeException("Paciente no encontrado"));

        mockMvc.perform(get("/api/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearPaciente_Success() throws Exception {
        when(pacienteService.crearPaciente(any(PacienteRequest.class)))
                .thenReturn(pacienteResponse);

        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteRequest)))
                .andExpect(status().isCreated());

        verify(pacienteService).crearPaciente(any(PacienteRequest.class));
    }

    @Test
    void testCrearPaciente_ValidationError() throws Exception {
        PacienteRequest invalidRequest = new PacienteRequest(
                "Carlos",
                "López",
                "9876543210",
                "carlos@example.com",
                1L
        );

        when(pacienteService.crearPaciente(any(PacienteRequest.class)))
                .thenReturn(pacienteResponse);

        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void testActualizarPaciente_Success() throws Exception {
        when(pacienteService.actualizarPaciente(anyLong(), any(PacienteRequest.class)))
                .thenReturn(pacienteResponse);

        mockMvc.perform(put("/api/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Carlos")));

        verify(pacienteService).actualizarPaciente(anyLong(), any(PacienteRequest.class));
    }

    @Test
    void testEliminarPaciente_Success() throws Exception {
        doNothing().when(pacienteService).eliminarPaciente(anyLong());

        mockMvc.perform(delete("/api/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(pacienteService).eliminarPaciente(1L);
    }

    @Test
    void testEliminarPaciente_NotFound() throws Exception {
        doThrow(new RuntimeException("Paciente no encontrado"))
                .when(pacienteService).eliminarPaciente(anyLong());

        mockMvc.perform(delete("/api/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
