package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAdminService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdminController {

    @Autowired
    private IEmpleadoService empleadoService;
    
    @Autowired
    private IAdminService adminService;

    // --- FILTRO DE SEGURIDAD INTERNO ---
    private boolean esAdminPuro(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        // Solo es válido si tiene el rol "ADMIN" y existe un ID en adminLogueadoId
        return "ADMIN".equals(rol) && adminId != null;
    }

    @GetMapping("/admin/index")
    public String indexAdmin(HttpSession session, Model model) {
        if (!esAdminPuro(session)) {
            return "redirect:/login"; 
        }
        model.addAttribute("listaEmpleados", empleadoService.findAll());
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        return "admin/index"; 
    }
    
    @GetMapping("/admin/gestion")
    public String mostrarGestionAdmin(HttpSession session, Model model) {
        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        
        return "admin/gestion"; 
    }
    
    @GetMapping("/admin/agregar_usuario")
    public String mostrarAgregarUsuario(HttpSession session, Model model) {
        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        
        model.addAttribute("nuevoEmpleado", new Empleado()); 
        
        return "admin/agregar_usuario"; 
    }
    
    @GetMapping("/admin/listado_usuarios")
    public String mostrarListadoUsuarios(HttpSession session, Model model) {
        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        
        // El admin puro ve a TODOS los empleados de la base de datos para gestionarlos
        model.addAttribute("empleados", empleadoService.findAll()); 
        
        return "admin/listado_usuarios"; 
    }
    
    @GetMapping("/admin/empleados/editar/{id}")
    public String editarEmpleadoForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdminPuro(session)) return "redirect:/login";

        // Buscamos el empleado por su ID
        Empleado empleadoExistente = empleadoService.findById(id);
        empleadoExistente.setPassword(null);
        
        // Lo mandamos al modelo con el MISMO nombre que usa el formulario de alta
        model.addAttribute("nuevoEmpleado", empleadoExistente);
        
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));

        // Reutilizamos la misma vista de agregar_usuario
        return "admin/agregar_usuario";
    }
    
    @PostMapping("/admin/empleados/guardar")
    public String guardarEmpleado(@Valid @ModelAttribute("nuevoEmpleado") Empleado empleado, BindingResult result, 
    		Model model, HttpSession session, jakarta.servlet.http.HttpServletRequest request, 
    		org.springframework.web.servlet.mvc.support.RedirectAttributes flash) {

    	boolean ignorarPassword = empleado.getId() != null && (empleado.getPassword() == null || empleado.getPassword().isEmpty());

        if (result.hasErrors() && !ignorarPassword) {
            Long adminId = (Long) session.getAttribute("adminLogueadoId");
            model.addAttribute("usuario", adminService.findById(adminId));
            return "admin/agregar_usuario"; 
        }

        try {
            empleadoService.save(empleado);
            String accion = (empleado.getId() == null) ? "creado" : "actualizado";
            flash.addFlashAttribute("success", "Empleado " + accion + " correctamente.");
        } catch (Exception e) {
        	System.out.println("Error al guardar: " + e.getMessage());
            flash.addFlashAttribute("error", "Error: No se pudo guardar el empleado en la base de datos.");
            return "redirect:/admin/listado_usuarios";
        }
        return "redirect:/admin/listado_usuarios";
    }

    @PostMapping("/admin/gestion/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes flash) {
        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        try {
            empleadoService.delete(id);
            flash.addFlashAttribute("success", "Usuario eliminado con éxito.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el usuario.");
        }
        
        return "redirect:/admin/listado_usuarios";
    }
}