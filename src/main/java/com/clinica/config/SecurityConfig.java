package com.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// Habilitar CSRF protection
			.csrf(csrf -> csrf.disable()) // TODO: Habilitar en producción con token CSRF

			// Autorización HTTP
			.authorizeHttpRequests(auth -> auth
				// Endpoints públicos
				.requestMatchers("/", "/api/public/**", "/actuator/health").permitAll()

				// Cualquier otro request requiere autenticación
				.anyRequest().authenticated()
			)

			// Deshabilitar form login y basic auth (usar JWT en futuro)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
