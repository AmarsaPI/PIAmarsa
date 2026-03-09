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
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF para permitir peticiones POST/PUT desde clientes externos
            .authorizeHttpRequests(auth -> auth
            		// 1.RUTAS WEB
            		.requestMatchers("/login/**", "/auth-check/**", "/index/**", "/horario_personal/**", "/gestion/**", "/fichar/**").permitAll()
                    .requestMatchers("/*.css", "/*.js", "/*.png").permitAll()

                    // 2. RUTAS API MÓVIL
                    // "login manual" por path variable(Poco seguro)
                    .requestMatchers("/api/login/**").permitAll() 
                    // Permitimos el acceso a horarios si lo necesita la móvil
                    .requestMatchers("/api/horarios/**").permitAll() 
                    
                    // 3. GESTIÓN 
                    // permitirlo aquí y que CADA UNO lo valide en su Controller.
                    .requestMatchers("/api/empleados/**").permitAll() 

                    .anyRequest().authenticated()
            
            /*.formLogin(form -> form
                // No usa formulario por defecto, usa la ruta /login que yo he creado en mi Controller".
                .loginPage("/login") 
                
                // Puerta abierta para todos
                .permitAll()*/
            )
            .logout(logout -> logout
                // Cuando alguien pulsa "Cerrar sesión", la sesión se destruye y lo mandamos de vuelta al login.
                // Al añadir "?logout" a la URL, el Controller puede detectar que viene de salir y mostrar un mensaje verde.
                .logoutSuccessUrl("/login?logout")

                // Cualquiera puede cerrar sesión
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