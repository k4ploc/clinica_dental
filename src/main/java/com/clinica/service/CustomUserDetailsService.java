package com.clinica.service;

import com.clinica.model.Usuario;
import com.clinica.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para cargar detalles de usuario desde la base de datos.
 * Implementa UserDetailsService para integración con Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Buscando usuario: {}", username);

        Usuario usuario = usuarioRepository.findActiveByUsername(username)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado o inactivo: {}", username);
                return new UsernameNotFoundException("Usuario no encontrado: " + username);
            });

        log.debug("Usuario encontrado: {} con {} roles", usuario.getUsername(), usuario.getRoles().size());
        return usuario;
    }
}
