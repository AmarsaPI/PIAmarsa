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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.dto.EmpleadoBalanceDTO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Ausencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.TipoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.EstadoAusencia;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAdminService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.ICalendarioLaboralService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAusenciaService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IBolsaHorasService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador encargado de gestionar las funciones del administrador.
 * Desde aquí se pueden gestionar empleados, ausencias y otros apartados.
 */
@Controller
public class AdminController {

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private IAdminService adminService;

    @Autowired
    private ICalendarioLaboralService calendarioService;

    @Autowired
    private IAusenciaService ausenciaService;

    @Autowired
    private IBolsaHorasService bolsaHorasService;

    /**
     * Comprueba si el usuario actual es un administrador válido.
     * 
     * @param session sesión actual del usuario
     * @return true si es administrador, false en caso contrario
     */
    private boolean esAdminPuro(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        return "ADMIN".equals(rol) && adminId != null;
    }

    /**
     * Muestra la página principal del administrador.
     * 
     * @param session sesión actual
     * @param model modelo de datos para la vista
     * @return vista principal del administrador
     */
    @GetMapping("/admin/index")
    public String indexAdmin(HttpSession session, Model model) {

        if (!esAdminPuro(session))
            return "redirect:/login";

        model.addAttribute("listaEmpleados", empleadoService.findAll());

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        model.addAttribute("usuario", adminService.findById(adminId));

        List<Empleado> empleados = empleadoService.findAll();

        List<EmpleadoBalanceDTO> listaReporte = new ArrayList<>();

        // Calcula balances de horas de cada empleado
        for (Empleado emp : empleados) {

            double balance = bolsaHorasService.calcularBolsaAnualAcumulada(emp);

            double horasPrevistas = bolsaHorasService.obtenerHorasPrevistasTotales(emp);

            double horasTrabajadas = horasPrevistas + balance;

            listaReporte.add(
                    new EmpleadoBalanceDTO(
                            emp.getNombre(),
                            horasTrabajadas,
                            horasPrevistas
                    )
            );
        }

        model.addAttribute("listaEmpleados", listaReporte);

        return "admin/index";
    }

    /**
     * Redirige al listado de usuarios.
     * 
     * @param session sesión actual
     * @return redirección al listado
     */
    @GetMapping("redirect:/admin/listado_usuarios")
    public String mostrarGestionAdmin(HttpSession session) {

        if (!esAdminPuro(session))
            return "redirect:/login";

        return "redirect:/admin/listado_usuarios";
    }

    /**
     * Muestra el formulario para añadir un nuevo empleado.
     * 
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista del formulario
     */
    @GetMapping("/admin/agregar_usuario")
    public String mostrarAgregarUsuario(HttpSession session, Model model) {

        if (!esAdminPuro(session))
            return "redirect:/login";

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        model.addAttribute("usuario", adminService.findById(adminId));

        model.addAttribute("listaCalendarios", calendarioService.findAll());

        model.addAttribute("nuevoEmpleado", new Empleado());

        return "admin/agregar_usuario";
    }

    /**
     * Muestra la lista de empleados.
     * 
     * @param mostrarTodos indica si se muestran empleados inactivos
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista con el listado de empleados
     */
    @GetMapping("/admin/listado_usuarios")
    public String mostrarListadoUsuarios(
            @RequestParam(value = "mostrarTodos", required = false, defaultValue = "false")
            boolean mostrarTodos,
            HttpSession session,
            Model model) {

        if (!esAdminPuro(session))
            return "redirect:/login";

        List<Empleado> empleados = mostrarTodos ?
                empleadoService.findAllIncluyendoInactivos() :
                empleadoService.findAll();

        model.addAttribute("empleados",
                (empleados != null) ? empleados : new ArrayList<>());

        model.addAttribute("mostrarTodos", mostrarTodos);

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        model.addAttribute("usuario", adminService.findById(adminId));

        return "admin/listado_usuarios";
    }

    /**
     * Muestra el formulario para editar un empleado.
     * 
     * @param id identificador del empleado
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista de edición
     */
    @GetMapping("/admin/empleados/editar/{id}")
    public String editarEmpleadoForm(@PathVariable Long id,
                                     HttpSession session,
                                     Model model) {

        if (!esAdminPuro(session))
            return "redirect:/login";

        Empleado empleadoExistente = empleadoService.findById(id);

        // Evita mostrar la contraseña
        empleadoExistente.setPassword(null);

        model.addAttribute("nuevoEmpleado", empleadoExistente);

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        model.addAttribute("usuario", adminService.findById(adminId));

        model.addAttribute("listaCalendarios", calendarioService.findAll());

        return "admin/agregar_usuario";
    }

    /**
     * Guarda un empleado nuevo o actualizado.
     * 
     * @param empleado datos del empleado
     * @param result resultado de validaciones
     * @param model modelo de datos
     * @param session sesión actual
     * @param request petición HTTP
     * @param flash mensajes temporales
     * @return redirección o vista correspondiente
     */
    @PostMapping("/admin/empleados/guardar")
    public String guardarEmpleado(
            @Valid @ModelAttribute("nuevoEmpleado") Empleado empleado,
            BindingResult result,
            Model model,
            HttpSession session,
            jakarta.servlet.http.HttpServletRequest request,
            RedirectAttributes flash) {

        boolean ignorarPassword =
                empleado.getId() != null &&
                (empleado.getPassword() == null || empleado.getPassword().isEmpty());

        // Comprueba errores de validación
        if (result.hasErrors() && !ignorarPassword) {

            Long adminId = (Long) session.getAttribute("adminLogueadoId");

            model.addAttribute("usuario", adminService.findById(adminId));

            model.addAttribute("listaCalendarios", calendarioService.findAll());

            return "admin/agregar_usuario";
        }

        try {

            empleadoService.save(empleado);

            String accion =
                    (empleado.getId() == null) ? "creado" : "actualizado";

            flash.addFlashAttribute(
                    "success",
                    "Empleado " + accion + " correctamente."
            );

        } catch (Exception e) {

            System.out.println("Error al guardar: " + e.getMessage());

            flash.addFlashAttribute(
                    "error",
                    "Error: No se pudo guardar el empleado en la base de datos."
            );

            return "redirect:/admin/listado_usuarios";
        }

        return "redirect:/admin/listado_usuarios";
    }

    /**
     * Muestra el formulario para asignar ausencias.
     * 
     * @param session sesión actual
     * @param model modelo de datos
     * @return vista del formulario
     */
    @GetMapping("/admin/asignar_ausencia")
    public String mostrarFormularioAsignar(HttpSession session, Model model) {

        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        model.addAttribute("usuario", adminService.findById(adminId));

        model.addAttribute("empleados", empleadoService.findAll());

        return "admin/asignar_ausencia";
    }

    /**
     * Guarda una ausencia oficial para un empleado.
     * 
     * @param empleadoId id del empleado
     * @param inicio fecha de inicio
     * @param fin fecha final
     * @param tipoStr tipo de ausencia
     * @param observaciones comentarios adicionales
     * @param session sesión actual
     * @param flash mensajes temporales
     * @return redirección correspondiente
     */
    @PostMapping("/admin/ausencias/guardar")
    public String guardarAusenciaOficial(
            @RequestParam("empleadoId") Long empleadoId,
            @RequestParam("fechaInicio")
            @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate inicio,

            @RequestParam("fechaFin")
            @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate fin,

            @RequestParam("tipo") String tipoStr,

            @RequestParam(value = "observaciones", required = false)
            String observaciones,

            HttpSession session,
            RedirectAttributes flash) {

        if (!esAdminPuro(session)) {
            return "redirect:/login";
        }

        try {

            Empleado empleado = empleadoService.findById(empleadoId);

            if (empleado == null) {

                flash.addFlashAttribute(
                        "error",
                        "El empleado seleccionado no existe."
                );

                return "redirect:/admin/asignar_ausencia";
            }

            // Comprueba que las fechas sean válidas
            if (inicio.isAfter(fin)) {

                flash.addFlashAttribute(
                        "error",
                        "La fecha de inicio no puede ser posterior a la de fin."
                );

                return "redirect:/admin/asignar_ausencia";
            }

            Ausencia ausencia = new Ausencia();

            ausencia.setEmpleado(empleado);
            ausencia.setFechaInicio(inicio);
            ausencia.setFechaFin(fin);
            ausencia.setObservaciones(observaciones);

            // Convierte el texto al enum correspondiente
            ausencia.setTipo(TipoAusencia.valueOf(tipoStr));

            // La ausencia queda aprobada automáticamente
            ausencia.setEstado(EstadoAusencia.APROBADA);

            ausenciaService.registrarAusencia(ausencia);

            flash.addFlashAttribute(
                    "success",
                    "Ausencia registrada correctamente y aplicada al cuadrante."
            );

            return "redirect:/admin/asignar_ausencia";

        } catch (Exception e) {

            flash.addFlashAttribute(
                    "error",
                    "No se pudo registrar la ausencia: " + e.getMessage()
            );
        }

        return "redirect:/admin/asignar_ausencia";
    }

    /**
     * Da de baja a un empleado.
     * 
     * @param id identificador del empleado
     * @param session sesión actual
     * @param flash mensajes temporales
     * @return redirección al listado
     */
    @PostMapping("/admin/gestion/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes flash) {

        if (!esAdminPuro(session))
            return "redirect:/login";

        try {

            // Marca el empleado como inactivo
            empleadoService.darDeBaja(id);

            flash.addFlashAttribute(
                    "success",
                    "Empleado dado de baja correctamente."
            );

        } catch (Exception e) {

            flash.addFlashAttribute(
                    "error",
                    "Error al procesar la baja."
            );
        }

        return "redirect:/admin/listado_usuarios";
    }
}