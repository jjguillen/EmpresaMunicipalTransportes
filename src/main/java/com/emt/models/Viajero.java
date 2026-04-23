package com.emt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Viajero {

    private String dni;
    private String nombre;
    private Integer edad;
    private String municipio;
    private TipoAbono tipoAbono;
    private Integer saldoPuntos;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DNI: ");
        sb.append(dni).append(" | ");
        sb.append(nombre).append(" | ");
        sb.append(edad).append(" años | ");
        sb.append(municipio).append(" | ");
        sb.append(tipoAbono).append(" | ");
        sb.append(saldoPuntos).append(" puntos ");
        return sb.toString();
    }
}
