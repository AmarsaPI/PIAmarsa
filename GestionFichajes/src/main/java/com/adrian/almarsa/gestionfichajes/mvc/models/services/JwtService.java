package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Rol;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * Clase para generar y verificar tokens de autenticación para app móvil
 */
@Service
public class JwtService {

    // Claves utilizada para firmar y encriptar el token
    private static final String GENERATOR = "Piamarsa_API";
    private static final String SECRET_KEY = "MiClaveSecretaSuperSeguraParaLaEmpresaPiamarsa";

    // Genera un token que expira en 1 día
    public String generarToken(String email, Long id, Rol rol) {
        return JWT.create()
                .withSubject(email) // Username (Email)
                .withIssuer(GENERATOR) // Firma del token
                .withClaim("id", id)
                .withClaim("rol", rol.toString()) // Guardar datos útiles dentro del token
                .withIssuedAt(new Date()) // Fecha y hora de creación del token
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000)) // Caducidad de 24 horas
                .sign(Algorithm.HMAC256(SECRET_KEY)); // Encriptación con HMAC256
    }

    // Valida el token y extrae la información descifrada.
    // Si es válida, devuelve un DecodedJWT, si no es LANZA EXCEPCION
    public DecodedJWT validarToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY)) // Validador de token con la clave secreta
                .withIssuer(GENERATOR) // Validador del token con la firma correcta (opcional)
                .build() // Construye el validador
                .verify(token); // Ejecuta las validaciones
    }
}