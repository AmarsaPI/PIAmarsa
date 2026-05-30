package com.adrian.almarsa.gestionfichajes.mvc.models.dto;

import java.time.LocalDate;

public class RangoFechasDTO {
    private LocalDate start;
    private LocalDate end;

    // Getters y Setters obligatorios
    public LocalDate getStart() { return start; }
    public void setStart(LocalDate start) { this.start = start; }
    public LocalDate getEnd() { return end; }
    public void setEnd(LocalDate end) { this.end = end; }
}
