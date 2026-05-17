package com.adrian.almarsa.gestionfichajes.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class GestionFichajesApplication {
	public static void main(String[] args) {
		SpringApplication.run(GestionFichajesApplication.class, args);
	}

}
