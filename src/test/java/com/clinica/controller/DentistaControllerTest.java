package com.clinica.controller;

import com.clinica.model.Dentista;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.dto.DentistaResponse;
import com.clinica.model.enums.Especialidad;
import com.clinica.service.DentistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(DentistaController.class)
class DentistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DentistaService dentistaService;

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
                "DENTISTA"
        );

        dentistaResponse = new DentistaResponse(
                1L,
                "Dr. Juan",
                "Pérez",
                "1234567890",
                "DENTISTA",
                null
        );

        dentista = Dentista.builder()
                .id(1L)
                .nombre("Dr. Juan")
                .apellido("Pérez")
                .telefono("1234567890")
                .especialidad(Especialidad.DENTISTA)
                .build();
    }

    @Test
    @WithMockUser
    void testGetDentistas() throws Exception {
        when(dentistaService.getDentistas()).thenReturn(List.of(dentistaResponse));

        mockMvc.perform(get("/dentista")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre", is("Dr. Juan")));

        verify(dentistaService).getDentistas();
    }

    @Test
    @WithMockUser
    void testObtenerDentista_Success() throws Exception {
        when(dentistaService.obtenerDentista(1L)).thenReturn(dentistaResponse);

        mockMvc.perform(get("/dentista/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Dr. Juan")));

        verify(dentistaService).obtenerDentista(1L);
    }

    @Test
    @WithMockUser
    void testObtenerDentista_NotFound() throws Exception {
        when(dentistaService.obtenerDentista(1L))
                .thenThrow(new RuntimeException("Dentista no encontrado"));

        mockMvc.perform(get("/dentista/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testCrearDentista_Success() throws Exception {
        when(dentistaService.createDentista(any(DentistaRequest.class)))
                .thenReturn(dentista);

        mockMvc.perform(post("/dentista")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dentistaRequest)))
                .andExpect(status().isCreated());

        verify(dentistaService).createDentista(any(DentistaRequest.class));
    }

    @Test
    @WithMockUser
    void testCrearDentista_ValidationError() throws Exception {
        DentistaRequest invalidRequest = new DentistaRequest(
                "Dr. Juan",
                "Pérez",
                "1234567890",
                "DENTISTA"
        );

        when(dentistaService.createDentista(any(DentistaRequest.class)))
                .thenReturn(dentista);

        mockMvc.perform(post("/dentista")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testActualizarDentista_Success() throws Exception {
        when(dentistaService.actualizarDentista(anyLong(), any(DentistaRequest.class)))
                .thenReturn(dentistaResponse);

        mockMvc.perform(put("/dentista/1")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dentistaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Dr. Juan")));

        verify(dentistaService).actualizarDentista(anyLong(), any(DentistaRequest.class));
    }

    @Test
    @WithMockUser
    void testEliminarDentista_Success() throws Exception {
        doNothing().when(dentistaService).eliminarDentista(anyLong());

        mockMvc.perform(delete("/dentista/1")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(dentistaService).eliminarDentista(1L);
    }

    @Test
    @WithMockUser
    void testEliminarDentista_NotFound() throws Exception {
        doThrow(new RuntimeException("Dentista no encontrado"))
                .when(dentistaService).eliminarDentista(anyLong());

        mockMvc.perform(delete("/dentista/1")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

