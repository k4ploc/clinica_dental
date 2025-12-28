package com.clinica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio para cargar detalles de usuario desde la base de datos.
 * Implementa UserDetailsService para integración con Spring Security.
 * 
 * Delega a UserAuthenticationService que maneja correctamente las transacciones.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserAuthenticationService userAuthenticationService;

    public CustomUserDetailsService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("CustomUserDetailsService.loadUserByUsername llamado para: {}", username);
        return userAuthenticationService.loadUserForAuthentication(username);
    }
}
