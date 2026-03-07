package com.adrian.almarsa.gestionfichajes.mvc.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.almarsa.gestionfichajes.mvc.models.dao.IEmpleadoDAO;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private IEmpleadoDAO empleadoDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // Devolvemos directamente el empleado encontrado. 
        // Al implementar la interfaz UserDetails, Spring ya sabe dónde buscar la pass y el email.
        return empleadoDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + email));
    }
}