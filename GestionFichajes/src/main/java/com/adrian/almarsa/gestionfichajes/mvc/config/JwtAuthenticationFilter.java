package com.adrian.almarsa.gestionfichajes.mvc.config;

import com.adrian.almarsa.gestionfichajes.mvc.models.services.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro encargado de comprobar los tokens JWT enviados
 * en las peticiones de la aplicación.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    /**
     * Constructor del filtro.
     * 
     * @param jwtService servicio que valida y gestiona los tokens
     */
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Método que intercepta cada petición para comprobar si el token es válido.
     * Si el token es correcto, guarda la autenticación en Spring Security.
     * 
     * @param request petición recibida
     * @param response respuesta enviada
     * @param filterChain cadena de filtros de Spring
     * @throws ServletException error relacionado con el servlet
     * @throws IOException error de entrada o salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Obtiene la cabecera Authorization de la petición
        String authHeader = request.getHeader("Authorization");

        // Comprueba que exista y que empiece por Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extrae el token quitando el texto "Bearer "
            String token = authHeader.substring(7);

            try {

                // Valida el token recibido
                DecodedJWT decodedJWT = jwtService.validarToken(token);

                // Obtiene el email guardado en el token
                String email = decodedJWT.getSubject();

                // Obtiene el rol del usuario
                String rol = decodedJWT.getClaim("rol").toString();

                // Crea la lista de permisos del usuario
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + rol));

                // Crea el objeto de autenticación
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Guarda la autenticación en Spring Security
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {

                // Si el token falla, limpia el contexto de seguridad
                SecurityContextHolder.clearContext();
            }
        }

        // Continúa con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}