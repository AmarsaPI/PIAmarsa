package com.adrian.almarsa.gestionfichajes.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
	        .csrf(csrf -> csrf.disable()) 
	        .authorizeHttpRequests(auth -> auth
	            // 1. RUTAS WEB Y GESTIÓN
	            // Añadimos /admin/** para que no bloquee tu nuevo panel
	            .requestMatchers("/login/**", "/auth-check/**", "/index/**", "/horario_personal/**", 
	                             "/gestion/**", "/fichar/**", "/admin/**", "/convenio/**").permitAll()
	            
	            // 2. RECURSOS ESTÁTICOS
	            // Usamos /** para asegurar que pille carpetas como /css/style.css o /js/app.js
	            .requestMatchers("/css/**", "/js/**", "/images/**", "/*.css", "/*.js", "/*.png", "/logo.png").permitAll()

	            // 3. RUTAS API MÓVIL (Tu compañero)
	            .requestMatchers("/api/login/**", "/api/horarios/**", "/api/empleados/**").permitAll() 

	            // El resto requiere estar autenticado (aunque de momento permitas casi todo arriba)
	            .anyRequest().authenticated()
	        )
	        // Como tú controlas el login en tu Controller, comentamos el formLogin de Spring
	        /* .formLogin(form -> form.loginPage("/login").permitAll()) */
	        
	        .logout(logout -> logout
	            .logoutUrl("/logout") // Ruta que dispara el cierre
	            .logoutSuccessUrl("/login?logout")
	            .invalidateHttpSession(true) // Importante: borra la "mochila"
	            .deleteCookies("JSESSIONID")
	            .permitAll()
	        );

	    return http.build();
	}

    // Define el algoritmo de hashing para las contraseñas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}