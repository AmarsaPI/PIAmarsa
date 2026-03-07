package com.adrian.almarsa.gestionfichajes.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define la cadena de filtros de seguridad y permisos de rutas
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF para permitir peticiones POST/PUT desde clientes externos
            .authorizeHttpRequests(auth -> auth
                // Solo usuarios con rol ADMINISTRADOR pueden gestionar empleados
                .requestMatchers("/api/empleados/**").hasRole("ADMINISTRADOR")
                // El resto de la API requiere al menos estar autenticado
                .anyRequest().authenticated()
            )
            // Habilita el formulario de login por defecto de Spring
            .formLogin(Customizer.withDefaults())
            // Habilita autenticación básica (útil para pruebas en Postman)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Define el algoritmo de hashing para las contraseñas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}