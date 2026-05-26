package com.adrian.almarsa.gestionfichajes.mvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IAdminDAO; // DAO de la nueva entidad
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin; // Nueva entidad Admin
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Rol;

import jakarta.transaction.Transactional;

@Configuration
public class DataSeedConfig implements CommandLineRunner {

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    @Autowired
    private IAdminDAO adminDAO; 

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        // Crear empleado administrador
        String empAdminEmail = "admin@piamarsa.com";
        if (empleadoDAO.findByEmail(empAdminEmail).isEmpty()) {
            Empleado adminEmp = new Empleado();
            adminEmp.setNombre("Administrador");
            adminEmp.setEmail(empAdminEmail);
            adminEmp.setPassword(passwordEncoder.encode("piamarsa"));
            adminEmp.setRol(Rol.ADMINISTRADOR);
            try {
                empleadoDAO.save(adminEmp);
                System.out.println("--> Empleado con Rol Administrador creado.");
            } catch (Exception e) {
                System.err.println("!!! ERROR AL CREAR EMPLEADO ADMIN: " + e.getMessage());
                e.printStackTrace(); 
            }
        }

        // Crear Administrador RRHH
        String adminPuroEmail = "admin.amarsa@amarsa.com";
        if (adminDAO.findByEmail(adminPuroEmail).isEmpty()) {
            Admin adminPuro = new Admin();
            adminPuro.setNombre("Gestor Principal");
            adminPuro.setEmail(adminPuroEmail);
            adminPuro.setPassword(passwordEncoder.encode("pass.amarsa"));
            
            adminDAO.save(adminPuro);
            
            System.out.println("###################################################");
            System.out.println("#  NUEVA ENTIDAD ADMIN CREADA AUTOMÁTICAMENTE     #");
            System.out.println("#  Email: " + adminPuroEmail + "                         #");
            System.out.println("#  Pass:  pass.amarsa                             #");
            System.out.println("###################################################");
        }
    }
}