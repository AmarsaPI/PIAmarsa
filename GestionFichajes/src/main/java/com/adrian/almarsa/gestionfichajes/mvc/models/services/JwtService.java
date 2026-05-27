package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Rol;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * Servicio para la generación y validación de tokens JWT usados por la app móvil.
 */
@Service
public class JwtService {

    // Claves utilizada para firmar y encriptar el token
    private static final String GENERATOR = "Piamarsa_API";
    private static final String SECRET_KEY = "MiClaveSecretaSuperSeguraParaLaEmpresaPiamarsa";

    /**
     * Genera un token JWT con datos del usuario.
     * @param email email del usuario
     * @param id identificador del usuario
     * @param rol rol del usuario
     * @return token generado
     */
    public String generarToken(String email, Long id, Rol rol) {
        return JWT.create()
                .withSubject(email)
                .withIssuer(GENERATOR)
                .withClaim("id", id)
                .withClaim("rol", rol.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * Valida un token JWT y devuelve su contenido descodificado.
     * @param token token a validar
     * @return información descodificada del token
     */
    public DecodedJWT validarToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .withIssuer(GENERATOR)
                .build()
                .verify(token);
    }
}
