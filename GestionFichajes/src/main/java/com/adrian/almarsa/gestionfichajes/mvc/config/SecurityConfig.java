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

/**
 * Clase encargada de configurar toda la seguridad
 * de la aplicación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// Servicio utilizado para validar los tokens JWT
	private JwtService jwtService;

	/**
	 * Constructor de la configuración de seguridad.
	 * 
	 * @param jwtService servicio que gestiona los tokens JWT
	 */
	public SecurityConfig(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	/**
	 * Configura la seguridad de las rutas de la API.
	 * Utiliza autenticación mediante JWT.
	 * 
	 * @param api configuración de seguridad de Spring
	 * @return cadena de filtros configurada
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain securityFilterChainAPI(HttpSecurity api) {

		api
				// Aplica esta configuración solo a rutas /api
				.securityMatcher("/api/**")

				// Desactiva CSRF para la API
				.csrf(csrf -> csrf.disable())

				// La API no guarda sesiones en servidor
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Añade el filtro personalizado JWT
				.addFilterBefore(
						new JwtAuthenticationFilter(jwtService),
						UsernamePasswordAuthenticationFilter.class
				)

				// Configura permisos de acceso
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/login/**").permitAll()
						.anyRequest().authenticated()
				);

		return api.build();
	}

	/**
	 * Configura la seguridad general de la aplicación web.
	 * 
	 * @param http configuración de seguridad
	 * @return cadena de filtros configurada
	 * @throws Exception posible error durante la configuración
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	    http
	        // Desactiva protección CSRF
	        .csrf(csrf -> csrf.disable())

	        // Configura permisos de acceso
	        .authorizeHttpRequests(auth -> auth

	            // Rutas permitidas sin iniciar sesión
	            .requestMatchers(
	            		"/login/**",
	            		"/auth-check/**",
	            		"/index/**",
	            		"/horario_personal/**",
	            		"/gestion_empleados/**",
	            		"/fichar/**",
	            		"/admin/**",
	            		"/api/**",
	            		"/crear_plantilla",
	            		"/asignar_horario",
	            		"/plantillas/guardar",
	            		"/gestion_plantillas",
	            		"/vacaciones/**",
	            		"/calendario-global",
	            		"/convenio/**",
	            		"/historial_fichajes/**",
	            		"/solicitudes/**",
	            		"/solicitar-cambio",
	            		"/solicitar-cambio-horario",
	            		"/bolsa/**",
	            		"/horarios/pdf/**",
	            		"/web/**",
	            		"/historial_fichajes/pendientes/**",
	            		"/perfil",
	            		"/perfil/**"
	            ).permitAll()

	            // Recursos estáticos permitidos
	            .requestMatchers(
	            		"/css/**",
	            		"/js/**",
	            		"/images/**",
	            		"/*.css",
	            		"/*.js",
	            		"/*.png",
	            		"/logo.png",
	            		"/horarios.js",
	            		"/ayuda.js"
	            ).permitAll()

	            // El resto necesita autenticación
	            .anyRequest().authenticated()
	        )

	        // Configuración del logout
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login?logout")

	            // Elimina la sesión actual
	            .invalidateHttpSession(true)

	            // Borra la cookie de sesión
	            .deleteCookies("JSESSIONID")

	            .permitAll()
	        ).formLogin(form -> form.loginPage("/login"));

	    return http.build();
	}

	/**
	 * Define el sistema de cifrado para las contraseñas.
	 * 
	 * @return codificador BCrypt
	 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}