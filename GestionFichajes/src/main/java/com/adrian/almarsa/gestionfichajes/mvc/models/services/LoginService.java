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

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    @Autowired
    private IAdminDAO adminDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // Buscamos en Admins
        Optional<Admin> admin = adminDAO.findByEmail(email);
        if (admin.isPresent()) return admin.get();

        // Si no, buscamos en Empleados
        return empleadoDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + email));
    }
    
    // Método para login manual en el Controller
    public Object loginManual(String email, String password) {
        UserDetails user = null;
        try {
            user = loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            return null;
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user; // Devuelve el Admin o Empleado
        }
        return null;
    }
}
