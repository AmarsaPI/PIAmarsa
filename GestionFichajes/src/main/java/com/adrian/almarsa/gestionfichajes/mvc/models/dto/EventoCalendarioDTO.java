package com.adrian.almarsa.gestionfichajes.mvc.models.dto;

/**
 * DTO utilizado para recibir rangos de fechas desde el cliente,
 * normalmente en operaciones relacionadas con calendarios o planificación.
 *
 * Representa un evento mediante dos cadenas: fecha/hora de inicio y fin.
 * Se usa cuando el frontend envía datos en formato texto (por ejemplo,
 * desde componentes de calendario o selectores de rango).
 */
public class EventoCalendarioDTO {

    private String start;
    private String end;

    /** @return fecha/hora de inicio del evento en formato texto */
    public String getStart() { return start; }

    /** @param start fecha/hora de inicio del evento */
    public void setStart(String start) { this.start = start; }

    /** @return fecha/hora de fin del evento en formato texto */
    public String getEnd() { return end; }

    /** @param end fecha/hora de fin del evento */
    public void setEnd(String end) { this.end = end; }
}
