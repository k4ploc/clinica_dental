package com.clinica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de autenticación.
 * 
 * Contiene el token JWT generado tras una autenticación exitosa.
 */
@Data
@NoArgsConstructor
public class AuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    public AuthResponse(String token) {
        this.accessToken = token;
    }
}
