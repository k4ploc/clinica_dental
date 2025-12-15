package com.clinica.controller;

import com.clinica.model.dto.PacienteRequest;
import com.clinica.model.dto.PacienteResponse;
import com.clinica.service.PacienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(PacienteController.class)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

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
                "carlos@example.com"
        );
    }

    @Test
    @WithMockUser
    void testListarPacientes() throws Exception {
        when(pacienteService.listarPacientes()).thenReturn(java.util.List.of(pacienteResponse));

        mockMvc.perform(get("/pacientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre", is("Carlos")));

        verify(pacienteService).listarPacientes();
    }

    @Test
    @WithMockUser
    void testObtenerPaciente_Success() throws Exception {
        when(pacienteService.obtenerPaciente(1L)).thenReturn(pacienteResponse);

        mockMvc.perform(get("/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Carlos")));

        verify(pacienteService).obtenerPaciente(1L);
    }

    @Test
    @WithMockUser
    void testObtenerPaciente_NotFound() throws Exception {
        when(pacienteService.obtenerPaciente(1L))
                .thenThrow(new RuntimeException("Paciente no encontrado"));

        mockMvc.perform(get("/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testCrearPaciente_Success() throws Exception {
        when(pacienteService.crearPaciente(any(PacienteRequest.class)))
                .thenReturn(pacienteResponse);

        mockMvc.perform(post("/pacientes")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Carlos")));

        verify(pacienteService).crearPaciente(any(PacienteRequest.class));
    }

    @Test
    @WithMockUser
    void testCrearPaciente_ValidationError() throws Exception {
        PacienteRequest invalidRequest = new PacienteRequest(
                "",  // nombre vacío
                "López",
                "9876543210",
                "carlos@example.com",
                1L
        );

        mockMvc.perform(post("/pacientes")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testActualizarPaciente_Success() throws Exception {
        when(pacienteService.actualizarPaciente(anyLong(), any(PacienteRequest.class)))
                .thenReturn(pacienteResponse);

        mockMvc.perform(put("/pacientes/1")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Carlos")));

        verify(pacienteService).actualizarPaciente(anyLong(), any(PacienteRequest.class));
    }

    @Test
    @WithMockUser
    void testEliminarPaciente_Success() throws Exception {
        doNothing().when(pacienteService).eliminarPaciente(anyLong());

        mockMvc.perform(delete("/pacientes/1")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(pacienteService).eliminarPaciente(1L);
    }

    @Test
    @WithMockUser
    void testEliminarPaciente_NotFound() throws Exception {
        doThrow(new RuntimeException("Paciente no encontrado"))
                .when(pacienteService).eliminarPaciente(anyLong());

        mockMvc.perform(delete("/pacientes/1")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}

