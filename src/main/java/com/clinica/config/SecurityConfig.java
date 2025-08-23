package com.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Desactiva CSRF
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll() // Permite TODO
				).formLogin(login -> login.disable()) // Quita el login por formulario
				.httpBasic(basic -> basic.disable()); // Quita el Basic Auth

		return http.build();
	}
}
