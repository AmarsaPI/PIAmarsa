package com.adrian.almarsa.gestionfichajes.mvc.models.dto;

//Clase para recibir los datos como string
public class EventoCalendarioDTO {
    private String start;
    private String end;

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }
    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
}