package com.emt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Viaje {

    private Long id;
    private Viajero viajero;
    private String lineaTransporte;
    private String origen;
    private String destino;
    private LocalDate fechaViaje;
    private LocalTime horaViaje;
    private Integer duracionMinutos;
    private Double precio;
    private boolean incidencia;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ID: ");
        sb.append(id).append(" | ");
        sb.append(viajero.getDni()).append(" | ");
        sb.append(lineaTransporte).append(" | ");
        sb.append(origen).append(" → ");
        sb.append(destino).append(" | ");
        sb.append(fechaViaje);
        sb.append(" ").append(horaViaje).append(" | ");
        sb.append(duracionMinutos).append(" min | ");
        sb.append(precio).append(" € | ");
        sb.append(" INCIDENCIA: ").append(incidencia);
        return sb.toString();
    }
}
