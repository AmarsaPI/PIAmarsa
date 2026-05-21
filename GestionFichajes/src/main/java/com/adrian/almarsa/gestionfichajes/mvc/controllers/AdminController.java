package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAdminService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.ICalendarioLaboralService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAusenciaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;

@Controller
public class AdminController {

    @Autowired
    private IEmpleadoService empleadoService;
    
    @Autowired
    private IAdminService adminService;
    
    @Autowired
    private ICalendarioLaboralService calendarioService;

    @Autowired
    private IAusenciaService ausenciaService; // 🌟 Inyectamos el servicio de ausencias

    // --- FILTRO DE SEGURIDAD INTERNO ---
    private boolean esAdminPuro(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        return "ADMIN".equals(rol) && adminId != null;
    }

    @GetMapping("/admin/index")
    public String indexAdmin(HttpSession session, Model model) {
        if (!esAdminPuro(session)) return "redirect:/login"; 
        model.addAttribute("listaEmpleados", empleadoService.findAll());
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        return "admin/index"; 
    }
    
    @GetMapping("/admin/gestion")
    public String mostrarGestionAdmin(HttpSession session, Model model) {
        if (!esAdminPuro(session)) return "redirect:/login";
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        return "admin/gestion"; 
    }
    
    @GetMapping("/admin/agregar_usuario")
    public String mostrarAgregarUsuario(HttpSession session, Model model) {
        if (!esAdminPuro(session)) return "redirect:/login";
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        model.addAttribute("listaCalendarios", calendarioService.findAll());
        model.addAttribute("nuevoEmpleado", new Empleado()); 
        return "admin/agregar_usuario"; 
    }
    
    @GetMapping("/admin/listado_usuarios")
    public String mostrarListadoUsuarios(HttpSession session, Model model) {
        if (!esAdminPuro(session)) return "redirect:/login";
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        model.addAttribute("empleados", empleadoService.findAll()); 
        return "admin/listado_usuarios"; 
    }
    
    @GetMapping("/admin/empleados/editar/{id}")
    public String editarEmpleadoForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdminPuro(session)) return "redirect:/login";
        Empleado empleadoExistente = empleadoService.findById(id);
        empleadoExistente.setPassword(null);
        model.addAttribute("nuevoEmpleado", empleadoExistente);
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId));
        model.addAttribute("listaCalendarios", calendarioService.findAll());
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
            model.addAttribute("listaCalendarios", calendarioService.findAll());
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
        if (!esAdminPuro(session)) return "redirect:/login";
        try {
            empleadoService.delete(id);
            flash.addFlashAttribute("success", "Usuario eliminado con éxito.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el usuario.");
        }
        return "redirect:/admin/listado_usuarios";
    }

    // =========================================================================
    // 🌟 NUEVO: VISTA PARA ASIGNAR BAJAS Y PERMISOS (SOLO ADMIN PURO)
    // =========================================================================
    @GetMapping("/admin/asignar_ausencia")
    public String mostrarFormularioAsignar(HttpSession session, Model model) {
        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        model.addAttribute("usuario", adminService.findById(adminId)); // Tu barra superior
        model.addAttribute("empleados", empleadoService.findAll());    // Lista para el select

        return "admin/asignar_ausencia"; // Plantilla dentro de templates/admin/
    }

    // =========================================================================
    // 🌟 NUEVO: PROCESAR EL GUARDADO DE LA BAJA / PERMISO
    // =========================================================================
    @PostMapping("/admin/ausencias/guardar")
    public String guardarAusenciaOficial(@RequestParam("empleadoId") Long empleadoId,
                                         @RequestParam("fechaInicio") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
                                         @RequestParam("fechaFin") @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fin,
                                         @RequestParam("tipo") String tipoStr,
                                         @RequestParam(value = "observaciones", required = false) String observaciones,
                                         HttpSession session,
                                         org.springframework.web.servlet.mvc.support.RedirectAttributes flash) {
        
        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        try {
            Empleado empleado = empleadoService.findById(empleadoId);
            if (empleado == null) {
                flash.addFlashAttribute("error", "El empleado seleccionado no existe.");
                return "redirect:/admin/asignar_ausencia";
            }

            if (inicio.isAfter(fin)) {
                flash.addFlashAttribute("error", "La fecha de inicio no puede ser posterior a la de fin.");
                return "redirect:/admin/asignar_ausencia";
            }

            // Construcción e inserción directa
            Ausencia ausencia = new Ausencia();
            ausencia.setEmpleado(empleado);
            ausencia.setFechaInicio(inicio);
            ausencia.setFechaFin(fin);
            ausencia.setObservaciones(observaciones);
            ausencia.setTipo(TipoAusencia.valueOf(tipoStr)); // Convierte el String al Enum correspondiente
            ausencia.setEstado(EstadoAusencia.APROBADA);    // El Admin aprueba ipso facto

            ausenciaService.registrarAusencia(ausencia);
            flash.addFlashAttribute("success", "Ausencia registrada correctamente y aplicada al cuadrante.");

            // Te redirige al cuadrante, justo en el mes donde empieza la baja
            return "redirect:/admin/asignar_ausencia";

        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo registrar la ausencia: " + e.getMessage());
        }

        return "redirect:/admin/asignar_ausencia";
    }
}