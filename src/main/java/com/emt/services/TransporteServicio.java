package com.emt.services;

import com.emt.models.TipoAbono;
import com.emt.models.Viaje;
import com.emt.models.Viajero;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TransporteServicio {

    private final List<Viajero> viajeros;
    private final List<Viaje> viajes;

    public TransporteServicio(List<Viajero> viajeros, List<Viaje> viajes) {
        this.viajeros = viajeros;
        this.viajes = viajes;
    }

    /**
     * Busca un viajero en la lista por su DNI.
     * @throws RuntimeException si no existe ningún viajero con ese DNI.
     */
    public Viajero buscarViajero(String dni) {
        return viajeros.stream()
                .filter(v -> v.getDni().equals(dni))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Viajero no encontrado: " + dni));
    }

    // --- Métodos de consulta de Streams --------------------------------------------------

    /**
     * Mostrar todos los viajes de una línea dada (por ejemplo `"L1"`), ordenados por hora
     * de forma ascendente.
     * @param linea
     * @return
     */
    public List<Viaje> getViajesPorLinea(String linea) {
        return viajes.stream()
                .filter(v -> v.getLineaTransporte().equals(linea))
                .toList();
    }

    /**
     * Obtener los **nombres sin repetición** de los viajeros que hayan tenido algún viaje
     * con incidencia, ordenados alfabéticamente.
     * @return
     */
    public List<String> getViajerosConIncidencias() {
        return viajes.stream()
                .filter(Viaje::isIncidencia)
                .map(Viaje::getViajero)
                .distinct()
                .map(Viajero::getNombre)
                .sorted()
                .toList();
    }

    /**
     * Encontrar el primer viaje registrado (el más temprano) de una fecha concreta
     * @param fecha
     * @return
     */
    public Optional<Viaje> getPrimerViaje(LocalDate fecha) {
        return viajes.stream()
                .filter(v -> v.getFechaViaje().equals(fecha))
                .min(Comparator.comparing(Viaje::getHoraViaje));
    }

    /**
     * Mostrar los viajes cuya duración supere el número de minutos dado, ordenados
     * de mayor a menor duración
     * @param minutos
     * @return
     */
    public List<Viaje> getViajesLargos(int minutos) {
        return viajes.stream()
                .filter(v -> v.getDuracionMinutos() > minutos)
                .sorted(Comparator.comparing(Viaje::getDuracionMinutos).reversed())
                .toList();
    }

    /**
     *
     * Obtener los 5 viajes que más han durado, mostrando linea, origen, destino,
     * fecha y hora, de mayor a menor.
     * de mayor a menor duración
     * @return
     */
    public List<String> getTop5ViajesPorDuracion() {
        return viajes.stream()
                .sorted(Comparator.comparing(Viaje::getDuracionMinutos).reversed())
                .limit(5)
                .map(v -> {
                    StringBuffer sb = new StringBuffer();
                    sb.append(v.getLineaTransporte()).append(", ");
                    sb.append(v.getOrigen()).append(" -> ").append(v.getDestino()).append(", ");
                    sb.append(v.getFechaViaje()).append(" ").append(v.getHoraViaje());
                    return sb.toString();
                })
                .toList();
    }

    /**
     * Crear un mapa donde la clave sea el **nombre del viajero** y el valor sea la **suma
     * total gastada** en todos sus viajes.
     * @return
     */
    public Map<String, Double> getGastoTotalPorViajero() {
        return viajes.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getViajero().getNombre(),
                        Collectors.summingDouble(Viaje::getPrecio)
                ));
    }

    /**
     * Obtener la duración media de los viajes agrupada por línea de transporte.
     * @return
     */
    public Map<String, Double> getDuracionMediaPorLinea() {
        return viajes.stream()
                .collect(Collectors.groupingBy(Viaje::getLineaTransporte,
                        Collectors.averagingInt(Viaje::getDuracionMinutos)));
    }

    /**
     * Crear un mapa donde la clave sea el **mes** (valor numérico 1–12) y el valor sea
     * el número de viajes realizados ese mes. Mostrar ordenado por mes.
     * @return
     */
    public Map<Integer, Long> getViajesPorMes() {
        return viajes.stream()
                .collect(Collectors.groupingBy(v -> v.getFechaViaje().getMonthValue(),
                        Collectors.counting()));
    }

    /**
     * Municipios que tengan incidencias ordenados alfabéticamente.
     * @return
     */
    public Set<String> getMunicipiosConIncidencias() {
        return viajes.stream()
                .filter(Viaje::isIncidencia)
                .map(Viaje::getOrigen)
                .distinct()
                .collect(Collectors.toSet());
    }

    /**
     * Calcular y *mostrar* las estadísticas sobre la duración de todos los viajes
     */
    public void getEstadisticasDuracion() {
        IntSummaryStatistics ism = viajes.stream()
                .mapToInt(Viaje::getDuracionMinutos)
                .summaryStatistics();
        IO.println("Duración media: " + ism.getAverage() + " minutos");
        IO.println("Duración mínima: " + ism.getMin() + " minutos");
        IO.println("Duración máxima: " + ism.getMax() + " minutos");
        IO.println("Duración total: " + ism.getSum() + " minutos");
    }

    /**
     * Obtener todos los viajes cuyo precio sea menor o igual al umbral dado, ordenados por precio ascendente
     * y mostrando línea, origen, destino y precio.
     * @param precioMax
     * @return
     */
    public List<String> getViajesBaratos(double precioMax) {
        return viajes.stream()
                .filter(v -> v.getPrecio() <= precioMax)
                .sorted(Comparator.comparing(Viaje::getDuracionMinutos))
                .map(v -> v.getLineaTransporte() + " | " + v.getOrigen() + " -> " +
                        v.getDestino() + " | " + v.getPrecio() + "€")
                .distinct()
                .toList();
    }

    /**
     * Verificar si **todos** los viajeros con tipo de abono `ANUAL` tienen un saldo de
     * puntos superior al valor dad
     * @param puntos
     * @return
     */
    public boolean todosAnualesSuperanPuntos(int puntos) {
        return viajes.stream()
                .filter(v -> v.getViajero().getTipoAbono().equals(TipoAbono.ANUAL))
                .allMatch(v -> v.getViajero().getSaldoPuntos() >= puntos);
    }

    /**
     * Dado un mes (valor numérico 1–12), mostrar el gasto total de cada viajero que
     * haya realizado algún viaje ese mes.
     * @param mes
     * @return
     */
    public Map<String, Double> getGastoPorViajeroEnMes(int mes) {
        return viajes.stream()
                .filter(v -> v.getFechaViaje().getMonthValue() == mes)
                .collect(Collectors.groupingBy(v -> v.getViajero().getNombre(),
                        Collectors.summingDouble(Viaje::getPrecio)));
    }

    /**
     * Obtener la línea de transporte que ha acumulado más viajes con incidencia.
     * @return
     */
    public Optional<String> getLineaConMasIncidencias() {
        Map<String, Long> lineas = viajes.stream()
                .filter(Viaje::isIncidencia)
                .collect(Collectors.groupingBy(Viaje::getLineaTransporte,
                        Collectors.counting()));

        return lineas.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    /**
     * Obtener los nombres de los viajeros con tipo de abono `OCASIONAL` cuyo gasto
     * total acumulado supere el umbral dado.
     * @param umbral
     * @return
     */
    public List<String> getOcasionalesGastoAlto(double umbral) {
        return viajes.stream()
                .filter(v -> v.getViajero().getTipoAbono().equals(TipoAbono.OCASIONAL))
                .collect(Collectors.groupingBy(Viaje::getViajero,
                        Collectors.summingDouble(Viaje::getPrecio)))
                .entrySet().stream()
                .filter(e -> e.getValue() > umbral)
                .map(e -> e.getKey().getNombre())
                .toList();
    }
















}
