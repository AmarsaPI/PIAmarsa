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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtra los tokens válidos y los guarda en SpringSecurity
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // Valida y guarda el token si no dispara una EXCEPCION
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Obtiene la clave Authorization de la cabecera de la petición
        String authHeader = request.getHeader("Authorization");

        // En caso de que exista la cabecera y que comience con Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extrae el token puro
            String token = authHeader.substring(7);

            // Intenta decodificar el token. Si no es válido SALTA UNA EXCEPCION
            try {
                DecodedJWT decodedJWT = jwtService.validarToken(token);

                // Extrae datos del token
                String email = decodedJWT.getSubject();
                String rol = decodedJWT.getClaim("rol").toString();

                // Genera una lista de autoridades (roles) que tiene el usuario
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                // Genera el token con el identificador (email) y los roles
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                // Guarda el token en el Security Context de Spring
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                // Elimina rastros de este token del Security Context
                SecurityContextHolder.clearContext();
            }
        }

        // Pasa al siguiente filtro del controlador
        filterChain.doFilter(request, response);
    }
}
