package com.adrian.almarsa.gestionfichajes.mvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Rol;

@Configuration
public class DataSeedConfig implements CommandLineRunner {

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@piamarsa.com";

        // 1. Verificamos si el administrador ya existe para no duplicarlo
        if (empleadoDAO.findByEmail(adminEmail).isEmpty()) {
            
            Empleado admin = new Empleado();
            admin.setNombre("Administrador");
            admin.setEmail(adminEmail);
            
            // 2. Ciframos la contraseña 'piamarsa' usando el BCryptPasswordEncoder del sistema
            admin.setPassword(passwordEncoder.encode("piamarsa"));
            admin.setRol(Rol.ADMINISTRADOR);
            
            // 3. Guardamos en la base de datos
            empleadoDAO.save(admin);
            
            System.out.println("###################################################");
            System.out.println("#  USUARIO ADMINISTRADOR CREADO AUTOMÁTICAMENTE   #");
            System.out.println("#  Email: " + adminEmail + "                        #");
            System.out.println("#  Pass:  piamarsa                                #");
            System.out.println("###################################################");
        } else {
            System.out.println("--> El usuario administrador ya existe en la base de datos.");
        }
    }
}