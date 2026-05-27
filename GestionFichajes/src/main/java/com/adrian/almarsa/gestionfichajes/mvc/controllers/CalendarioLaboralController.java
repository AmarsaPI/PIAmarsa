package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.CalendarioLaboral;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Festivo;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAdminService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.ICalendarioLaboralService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador encargado de gestionar los calendarios laborales.
 * Permite crear, editar y eliminar calendarios.
 */
@Controller
public class CalendarioLaboralController {
	
	@Autowired 
    private ICalendarioLaboralService calendarioService;
	
	@Autowired
	private IAdminService adminService;
	
	/**
	 * Comprueba si el usuario actual es administrador.
	 * 
	 * @param session sesión actual
	 * @return true si es administrador, false si no lo es
	 */
	private boolean esAdminPuro(HttpSession session) {

        String rol = (String) session.getAttribute("rol");

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        // Comprueba que el usuario tenga rol ADMIN
        return "ADMIN".equals(rol) && adminId != null;
    }
	
	/**
	 * Muestra el listado de calendarios laborales.
	 * 
	 * @param model modelo de datos
	 * @param session sesión actual
	 * @return vista con los calendarios
	 */
	
	// Muestra todos los calendarios disponibles
	@GetMapping("/admin/calendarios_laborales")
	public String listarCalendarios(Model model, HttpSession session) {

		if (!esAdminPuro(session)) {
            return "redirect:/login"; 
        }

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        model.addAttribute("usuario", adminService.findById(adminId));

	    model.addAttribute("calendarios", calendarioService.findAll());

	    return "admin/calendarios_laborales";
	}
	
	/**
	 * Muestra el formulario para crear un nuevo calendario.
	 * 
	 * @param model modelo de datos
	 * @return vista del formulario
	 */
	
	// Carga el formulario para crear un calendario
	@GetMapping("/admin/agregar_calendario")
	public String formularioNuevo(Model model) {

	    CalendarioLaboral calendario = new CalendarioLaboral();

	    calendario.setFestivos(new ArrayList<>());

	    model.addAttribute("calendario", calendario);

	    return "admin/agregar_calendario";
	}
	
	/**
	 * Guarda un calendario nuevo o editado.
	 * 
	 * @param calendario calendario recibido del formulario
	 * @param flash mensajes temporales
	 * @return redirección al listado
	 */
	
	// Guarda el calendario en la base de datos
    @PostMapping("/admin/guardar_calendario")
    public String guardar(@ModelAttribute("calendario") CalendarioLaboral calendario,
                          RedirectAttributes flash) {
        
        // Asigna el calendario a cada festivo
        // para mantener la relación en la base de datos
        if (calendario.getFestivos() != null) {

            for (Festivo festivo : calendario.getFestivos()) {

                festivo.setCalendario(calendario);
            }
        }

        calendarioService.save(calendario);

        flash.addFlashAttribute("success", "Calendario guardado con éxito");

        return "redirect:/admin/calendarios_laborales";
    }

    /**
     * Carga los datos de un calendario para editarlo.
     * 
     * @param id id del calendario
     * @param model modelo de datos
     * @param flash mensajes temporales
     * @return vista de edición
     */
    
    // Carga un calendario existente para editarlo
    @GetMapping("/admin/editar_calendario/{id}")
    public String editar(@PathVariable(value = "id") Long id,
                         Model model,
                         RedirectAttributes flash) {

        CalendarioLaboral calendario = null;

        if (id > 0) {

            calendario = calendarioService.findById(id);

            // Comprueba si el calendario existe
            if (calendario == null) {

                flash.addFlashAttribute(
                        "error",
                        "El calendario no existe"
                );

                return "redirect:/admin/calendarios_laborales";
            }

        } else {

            flash.addFlashAttribute(
                    "error",
                    "El id del calendario no es válido"
            );

            return "redirect:/admin/calendarios_laborales";
        }

        model.addAttribute("calendario", calendario);

        model.addAttribute(
                "titulo",
                "Editar Calendario: " + calendario.getNombre()
        );

        // Usa el mismo formulario de crear calendario
        return "admin/agregar_calendario";
    }

    /**
     * Elimina un calendario laboral.
     * 
     * @param id id del calendario
     * @param flash mensajes temporales
     * @return redirección al listado
     */
    
    // Elimina un calendario de la base de datos
    @GetMapping("/admin/eliminar_calendario/{id}")
    public String eliminar(@PathVariable(value = "id") Long id,
                           RedirectAttributes flash) {

        if (id > 0) {

            calendarioService.delete(id);

            flash.addFlashAttribute(
                    "success",
                    "Calendario eliminado correctamente"
            );
        }

        return "redirect:/admin/calendarios_laborales";
    }
}