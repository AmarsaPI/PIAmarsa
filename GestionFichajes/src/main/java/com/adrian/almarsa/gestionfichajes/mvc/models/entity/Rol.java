package com.adrian.almarsa.gestionfichajes.mvc.models.entity;

// Define los tipos de acceso permitidos en el sistema
public enum Rol {

    // Usuario estándar: solo puede gestionar sus propios fichajes
    EMPLEADO,
    
    // Usuario con control total: accede a la gestión de todos los empleados
    ADMINISTRADOR

}