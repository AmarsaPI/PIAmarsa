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
import com.adrian.almarsa.gestionfichajes.mvc.models.services.BolsaHorasServiceImpl;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IAdminService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IBolsaHorasService;
import com.adrian.almarsa.gestionfichajes.mvc.models.services.IEmpleadoService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/bolsa")
public class BolsaHorasController {

    @Autowired
    private IBolsaHorasService bolsaHorasService;
    
    @Autowired
    private IEmpleadoService empleadoService;
    
    @Autowired
    private IAdminService adminService;
    

    BolsaHorasController(BolsaHorasServiceImpl bolsaHorasServiceImpl) {
    }

    // Este método devuelve la vista con el saldo inyectado
    @GetMapping("/resumen")
    public String verResumenBolsa(Model model, HttpSession session) {
        Long empId = (Long) session.getAttribute("usuarioLogueadoId");
        Empleado emp = empleadoService.findById(empId);
        
        // Calculamos el saldo acumulado (la Bolsa de Horas)
        double saldoTotal = bolsaHorasService.calcularBolsaAnualAcumulada(emp);
        
        model.addAttribute("saldoTotal", saldoTotal);
        return "bolsa/resumen"; // Nombre de tu vista HTML
    }
    
    @GetMapping("/informe")
    public String listarBalances(Model model, HttpSession session) {
    	Long empId = (Long) session.getAttribute("usuarioLogueadoId");
        Long adminId = (Long) session.getAttribute("adminLogueadoId");

        // Si es Admin, cargamos el perfil de Admin, si es Empleado, el de Empleado
        if (adminId != null) {
            model.addAttribute("usuario", adminService.findById(adminId));
        } else if (empId != null) {
            model.addAttribute("usuario", empleadoService.findById(empId));
        } else {
            return "redirect:/login";
        }
        
        // 2. Obtener la lista de empleados para el informe
        List<Empleado> empleados = empleadoService.findAll();
        List<EmpleadoBalanceDTO> listaReporte = new ArrayList<>();

        for (Empleado emp : empleados) {
            double balance = bolsaHorasService.calcularBolsaAnualAcumulada(emp);
            double horasPrevistas = bolsaHorasService.obtenerHorasPrevistasTotales(emp); 
            double horasTrabajadas = horasPrevistas + balance;
            
            listaReporte.add(new EmpleadoBalanceDTO(emp.getNombre(), horasTrabajadas, horasPrevistas));
        }
        
        model.addAttribute("listaEmpleados", listaReporte);
        
        return "informe"; 
    }
}
