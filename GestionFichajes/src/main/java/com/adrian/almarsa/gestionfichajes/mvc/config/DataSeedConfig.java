package com.adrian.almarsa.gestionfichajes.mvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IAdminDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Rol;

import jakarta.transaction.Transactional;

/**
 * Clase que inserta algunos datos iniciales en la base de datos
 * cuando se inicia la aplicación.
 */
@Configuration
public class DataSeedConfig implements CommandLineRunner {

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    @Autowired
    private IAdminDAO adminDAO; 

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método que se ejecuta automáticamente al arrancar la aplicación.
     * Aquí se crean usuarios administradores si no existen.
     * 
     * @param args argumentos de inicio
     * @throws Exception posible error durante la ejecución
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        // Crear empleado administrador
        String empAdminEmail = "admin@piamarsa.com";

        // Comprueba si el empleado administrador ya existe
        if (empleadoDAO.findByEmail(empAdminEmail).isEmpty()) {

            Empleado adminEmp = new Empleado();

            adminEmp.setNombre("Administrador");
            adminEmp.setEmail(empAdminEmail);
            adminEmp.setPassword(passwordEncoder.encode("piamarsa"));
            adminEmp.setRol(Rol.ADMINISTRADOR);

            try {

                // Guarda el empleado administrador
                empleadoDAO.save(adminEmp);

                System.out.println("--> Empleado con Rol Administrador creado.");

            } catch (Exception e) {

                // Muestra el error si falla la creación
                System.err.println("!!! ERROR AL CREAR EMPLEADO ADMIN: " + e.getMessage());

                e.printStackTrace(); 
            }
        }

        // Crear administrador de RRHH
        String adminPuroEmail = "admin.amarsa@amarsa.com";

        // Comprueba si el administrador ya existe
        if (adminDAO.findByEmail(adminPuroEmail).isEmpty()) {

            Admin adminPuro = new Admin();

            adminPuro.setNombre("Gestor Principal");
            adminPuro.setEmail(adminPuroEmail);
            adminPuro.setPassword(passwordEncoder.encode("pass.amarsa"));

            // Guarda el administrador
            adminDAO.save(adminPuro);

            System.out.println("###################################################");
            System.out.println("#  NUEVA ENTIDAD ADMIN CREADA AUTOMÁTICAMENTE     #");
            System.out.println("#  Email: " + adminPuroEmail + "                         #");
            System.out.println("#  Pass:  pass.amarsa                             #");
            System.out.println("###################################################");
        }
    }
}