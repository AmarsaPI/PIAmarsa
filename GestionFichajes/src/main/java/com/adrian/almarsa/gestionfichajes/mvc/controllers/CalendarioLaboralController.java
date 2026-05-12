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

@Controller
public class CalendarioLaboralController {
	
	@Autowired 
    private ICalendarioLaboralService calendarioService;
	
	@Autowired
	private IAdminService adminService;
	
	private boolean esAdminPuro(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        Long adminId = (Long) session.getAttribute("adminLogueadoId");
        // Solo es válido si tiene el rol "ADMIN" y existe un ID en adminLogueadoId
        return "ADMIN".equals(rol) && adminId != null;
    }
	
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
	
	@GetMapping("/admin/agregar_calendario")
	public String formularioNuevo(Model model) {
	    CalendarioLaboral calendario = new CalendarioLaboral();
	    calendario.setFestivos(new ArrayList<>());
	    model.addAttribute("calendario", calendario);
	    return "admin/agregar_calendario";
	}
	
	// MÉTODO PARA GUARDAR (CREAR O MODIFICAR)
    @PostMapping("/admin/guardar_calendario")
    public String guardar(@ModelAttribute("calendario") CalendarioLaboral calendario, RedirectAttributes flash) {
        
        // Es vital asignar el objeto calendario a cada festivo antes de guardar
        // para que la relación en la base de datos se mantenga (llave foránea)
        if (calendario.getFestivos() != null) {
            for (Festivo festivo : calendario.getFestivos()) {
                festivo.setCalendario(calendario);
            }
        }

        calendarioService.save(calendario);
        flash.addFlashAttribute("success", "Calendario guardado con éxito");
        return "redirect:/admin/calendarios_laborales";
    }

    // MÉTODO PARA EDITAR (Carga el formulario con datos)
    @GetMapping("/admin/editar_calendario/{id}")
    public String editar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        CalendarioLaboral calendario = null;

        if (id > 0) {
            calendario = calendarioService.findById(id);
            if (calendario == null) {
                flash.addFlashAttribute("error", "El ID del calendario no existe en la base de datos");
                return "redirect:/admin/calendarios_laborales";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del calendario no puede ser cero");
            return "redirect:/admin/calendarios_laborales";
        }

        model.addAttribute("calendario", calendario);
        model.addAttribute("titulo", "Editar Calendario: " + calendario.getNombre());
        return "admin/agregar_calendario"; // Usamos el mismo HTML de agregar
    }

    // MÉTODO PARA ELIMINAR
    @GetMapping("/admin/eliminar_calendario/{id}")
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            calendarioService.delete(id);
            flash.addFlashAttribute("success", "Calendario eliminado con éxito");
        }
        return "redirect:/admin/calendarios_laborales";
    }

}
