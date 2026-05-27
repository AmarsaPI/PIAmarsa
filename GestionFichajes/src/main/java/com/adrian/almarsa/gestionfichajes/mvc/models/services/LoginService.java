package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IAdminDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Admin;

/**
 * Servicio de autenticación que carga usuarios (admins o empleados)
 * y valida credenciales para el inicio de sesión.
 */
@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    @Autowired
    private IAdminDAO adminDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Carga un usuario por email para Spring Security.
     * @param email email del usuario
     * @return detalles del usuario
     * @throws UsernameNotFoundException si no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Admin> admin = adminDAO.findByEmail(email);
        if (admin.isPresent()) return admin.get();

        return empleadoDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + email));
    }

    /**
     * Valida credenciales manualmente para login desde el controlador.
     * @param email email del usuario
     * @param password contraseña sin cifrar
     * @return usuario autenticado o null si falla
     */
    public Object loginManual(String email, String password) {
        UserDetails user = null;

        try {
            user = loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            return null;
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}
