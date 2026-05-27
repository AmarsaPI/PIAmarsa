package com.adrian.almarsa.gestionfichajes.mvc.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.adrian.almarsa.gestionfichajes.mvc.models.dto.EmpleadoBalanceDTO;
import com.adrian.almarsa.gestionfichajes.mvc.models.entity.Empleado;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAdminService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IBolsaHorasService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador encargado de mostrar la información
 * relacionada con la bolsa de horas de los empleados.
 */
@Controller
@RequestMapping("/bolsa")
public class BolsaHorasController {

    @Autowired
    private IBolsaHorasService bolsaHorasService;

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private IAdminService adminService;

    /**
     * Muestra el resumen de la bolsa de horas del empleado.
     * 
     * @param model modelo de datos
     * @param session sesión actual
     * @return vista con el resumen de horas
     */
    
    // Muestra el saldo total de horas del empleado
    @GetMapping("/resumen")
    public String verResumenBolsa(Model model, HttpSession session) {

        Long empId = (Long) session.getAttribute("usuarioLogueadoId");

        Empleado emp = empleadoService.findById(empId);

        // Calcula el saldo acumulado de horas
        double saldoTotal =
                bolsaHorasService.calcularBolsaAnualAcumulada(emp);

        model.addAttribute("saldoTotal", saldoTotal);

        return "bolsa/resumen";
    }

    /**
     * Muestra un informe general con el balance de horas
     * de todos los empleados.
     * 
     * @param model modelo de datos
     * @param session sesión actual
     * @return vista del informe
     */
    
    // Genera un informe con las horas trabajadas y previstas
    @GetMapping("/informe")
    public String listarBalances(Model model, HttpSession session) {

        Long empId = (Long) session.getAttribute("usuarioLogueadoId");

        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        // Comprueba qué tipo de usuario ha iniciado sesión
        if (adminId != null) {

            model.addAttribute(
                    "usuario",
                    adminService.findById(adminId)
            );

        } else if (empId != null) {

            model.addAttribute(
                    "usuario",
                    empleadoService.findById(empId)
            );

        } else {

            return "redirect:/login";
        }

        // Obtiene todos los empleados
        List<Empleado> empleados = empleadoService.findAll();

        List<EmpleadoBalanceDTO> listaReporte = new ArrayList<>();

        // Calcula el balance de horas de cada empleado
        for (Empleado emp : empleados) {

            double balance =
                    bolsaHorasService.calcularBolsaAnualAcumulada(emp);

            double horasPrevistas =
                    bolsaHorasService.obtenerHorasPrevistasTotales(emp);

            double horasTrabajadas =
                    horasPrevistas + balance;

            listaReporte.add(
                    new EmpleadoBalanceDTO(
                            emp.getNombre(),
                            horasTrabajadas,
                            horasPrevistas
                    )
            );
        }

        model.addAttribute("listaEmpleados", listaReporte);

        return "informe";
    }
}