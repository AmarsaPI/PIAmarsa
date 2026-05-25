package com.adrian.almarsa.gestionfichajes.mvc.models.dto;

public class EmpleadoBalanceDTO {
    private String nombre;
    private double horasTrabajadas;
    private double horasPrevistas;
    
    // Constructor
    public EmpleadoBalanceDTO(String nombre, double horasTrabajadas, double horasPrevistas) {
        this.nombre = nombre;
        this.horasTrabajadas = horasTrabajadas;
        this.horasPrevistas = horasPrevistas;
    }
    
    // Getters
    public String getNombre() { return nombre; }
    public double getHorasTrabajadas() { return horasTrabajadas; }
    public double getHorasPrevistas() { return horasPrevistas; }
}