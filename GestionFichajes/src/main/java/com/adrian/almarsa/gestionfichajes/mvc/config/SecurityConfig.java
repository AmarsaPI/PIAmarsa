package com.adrian.almarsa.gestionfichajes.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.adrian.almarsa.gestionfichajes.mvc.models.services.JwtService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define la cadena de filtros de seguridad y permisos de rutas
	// Necesario para pasárselo a JwtAuthenticationFilter
		private JwtService jwtService;

		public SecurityConfig(JwtService jwtService) {
			this.jwtService = jwtService;
		}

		// Define la cadena de filtros de seguridad y permisos de rutas para la API
		// Se ejecuta en primer lugar para
		@Bean
		@Order(1)
		public SecurityFilterChain securityFilterChainAPI(HttpSecurity api) {
			api
					// Filtro de desvío necesario para entrar en las rutas de la API
					.securityMatcher("/api/**")
					.csrf(csrf -> csrf.disable())

					// Fuerza a no guardar el estado de la session en el servidor
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

					// Filtro personalizado a aplicar en primera instancia que verifica que se esté autenticado
					.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)

					// Filtro a aplicar en segunda instancia
					.authorizeHttpRequests(auth -> auth
							.requestMatchers("/login/**").permitAll()
							.anyRequest().authenticated()
					);

			return api.build();
		}


	    // Define la cadena de filtros de seguridad y permisos de rutas para la Web
		// Se ejecuta en segundo lugar por, por descarte porque no tiene .securityMatcher
	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable()) 
	        .authorizeHttpRequests(auth -> auth
	            // 1. RUTAS WEB Y GESTIÓN
	            // Añadimos /admin/** para que no bloquee tu nuevo panel
	            .requestMatchers("/login/**", "/auth-check/**", "/index/**", "/horario_personal/**", 
	                             "/gestion_empleados/**", "/fichar/**", "/admin/**", "/api/**", "/crear_plantilla", "/asignar_horario", "/plantillas/guardar", "/gestion_plantillas"
	                             , "/vacaciones/**", "/calendario-global", "/convenio/**", "/historial_fichajes/**", "/solicitudes/**", "/solicitar-cambio", "/solicitar-cambio-horario", "/bolsa/**").permitAll()
	            
	            // 2. RECURSOS ESTÁTICOS
	            // Usamos /** para asegurar que pille carpetas como /css/style.css o /js/app.js
	            .requestMatchers("/css/**", "/js/**", "/images/**", "/*.css", "/*.js", "/*.png", "/logo.png", "/horarios.js").permitAll()

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
}