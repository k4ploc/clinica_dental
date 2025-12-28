package com.clinica.service;

import com.clinica.model.Usuario;
import com.clinica.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio transaccional para garantizar que las operaciones de
 * autenticación ocurran dentro de un contexto transaccional adecuado.
 */
@Service
public class UserAuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthenticationService.class);

    private final UsuarioRepository usuarioRepository;

    public UserAuthenticationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga usuario activo con sus roles inicializados.
     * Los roles ya vienen cargados por el JOIN FETCH en la query.
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserForAuthentication(String username) throws UsernameNotFoundException {
        log.debug("Cargando usuario para autenticación: {}", username);

        Usuario usuario = usuarioRepository.findActiveByUsername(username)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado o inactivo: {}", username);
                return new UsernameNotFoundException("Usuario no encontrado: " + username);
            });

        log.debug("Usuario cargado exitosamente: {} con {} roles",
            usuario.getUsername(), usuario.getRoles().size());

        return usuario;
    }
}
