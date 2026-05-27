package com.adrian.almarsa.gestionfichajes.mvc.models.dto;

/**
 * DTO utilizado para representar el balance de horas de un empleado.
 * 
 * Contiene el nombre del empleado, las horas realmente trabajadas
 * y las horas previstas según su planificación. Se usa normalmente
 * en informes, resúmenes o paneles de control.
 */
public class EmpleadoBalanceDTO {

    private String nombre;
    private double horasTrabajadas;
    private double horasPrevistas;

    /**
     * Crea un nuevo objeto de balance de horas para un empleado.
     *
     * @param nombre nombre del empleado
     * @param horasTrabajadas total de horas registradas mediante fichajes
     * @param horasPrevistas total de horas planificadas en su horario
     */
    public EmpleadoBalanceDTO(String nombre, double horasTrabajadas, double horasPrevistas) {
        this.nombre = nombre;
        this.horasTrabajadas = horasTrabajadas;
        this.horasPrevistas = horasPrevistas;
    }

    /** @return nombre del empleado */
    public String getNombre() { return nombre; }

    /** @return horas realmente trabajadas */
    public double getHorasTrabajadas() { return horasTrabajadas; }

    /** @return horas previstas según planificación */
    public double getHorasPrevistas() { return horasPrevistas; }
}
