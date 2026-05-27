package com.adrian.almarsa.gestionfichajes.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Punto de entrada principal de la aplicación de gestión de fichajes.
 * Inicia el contexto de Spring Boot y arranca todos los servicios.
 */
@SpringBootApplication
public class GestionFichajesApplication {

    /**
     * Método principal que lanza la aplicación.
     * @param args argumentos de ejecución
     */
    public static void main(String[] args) {
        SpringApplication.run(GestionFichajesApplication.class, args);
    }
}
