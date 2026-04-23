package com.emt;

import com.emt.models.Viaje;
import com.emt.models.Viajero;
import com.emt.services.TransporteServicio;
import com.emt.utils.CsvLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void menu() {
        IO.println("----------- RED DE TRANSPORTE PÚBLICO -------------");
        IO.println("Elige opción: ");
        IO.println("1. Viajes de una línea");
        IO.println("2. Viajeros con incidencias");
        IO.println("3. Primer viaje del día");
        IO.println("4. Viajes largos");
        IO.println("5. Top 5 viajes más largos en duración");
        IO.println("6. Gasto total por viajero");
        IO.println("7. Duración media por línea");
        IO.println("8. Número de viajes por mes");
        IO.println("9. Municipios con incidencias");
        IO.println("10. Estadísticas duración viajes");
        IO.println("11. Viajes gratuitos o de bajo coste");
        IO.println("12. Todos los viajeros con abono ANUAL superan 100 puntos");
        IO.println("13. Mostrar gasto de los viajeros en un mes");
        IO.println("14. Línea con más incidencias");
        IO.println("15. Viajeros con abono OCASIONAL y gasto alto");
        IO.println("16. Salir");
    }

    public static void main(String[] args) {

        List<Viajero> viajeros = null;
        List<Viaje> viajes = null;
        int opcion = 0;

        try {
            viajeros = CsvLoader.cargarViajeros("src/main/resources/viajeros.csv");
            viajes   = CsvLoader.cargarViajes("src/main/resources/viajes.csv", viajeros);
        } catch (IOException e) {
            log.error("Error al cargar los datos {}", e.getMessage());
        }

        TransporteServicio servicio = new TransporteServicio(viajeros, viajes);

        do {
            try {
                menu();
                opcion = Integer.parseInt(IO.readln());
                switch (opcion) {
                    case 1 -> {
                        IO.println("== Consulta 1: Viajes de la línea ");
                        IO.println("Dime línea (L1, L2, L3, L4, L5): ");
                        String linea = IO.readln();
                        servicio.getViajesPorLinea(linea).forEach(System.out::println);
                    }
                    case 2 -> {
                        IO.println("== Consulta 2: Viajeros con incidencias");
                        servicio.getViajerosConIncidencias().forEach(System.out::println);
                    }
                    case 3 -> {
                        IO.println("== Consulta 3: Primer viaje del día");
                        servicio.getPrimerViaje(LocalDate.of(2025,2,21))
                                .ifPresent(System.out::println);
                    }
                    case 4 -> {
                        IO.println("== Consulta 4: Viajes largos");
                        IO.println("Dime minutos: ");
                        int minutos = Integer.parseInt(IO.readln());
                        servicio.getViajesLargos(minutos).forEach(System.out::println);
                    }
                    case 5 ->  {
                        IO.println("== Consulta 5: Top 5 viajes más largos en duración");
                        servicio.getTop5ViajesPorDuracion().forEach(System.out::println);
                    }
                    case 6 -> {
                        IO.println("== Consulta 6: Gasto total por viajero");
                        servicio.getGastoTotalPorViajero().forEach((nombre, gasto) -> {
                            IO.println(nombre + " gasta: " + gasto + "€");
                        });
                    }
                    case 7 -> {
                        IO.println("== Consulta 7: Duración media por línea");
                        servicio.getDuracionMediaPorLinea().forEach((linea, duracion) -> {
                            IO.println(linea + " tiene duración media: " + duracion + " minutos");
                        });
                    }
                    case 8 -> {
                        IO.println("== Consulta 8: Número de viajes por mes");
                        servicio.getViajesPorMes().forEach((mes, cantidad) -> {
                            IO.println("Mes: " + mes + " se han realizado " + cantidad + " viajes");
                        });
                    }
                    case 9 -> {
                        IO.println("== Consulta 9: Municipios con incidencias");
                        servicio.getMunicipiosConIncidencias().forEach(System.out::println);
                    }
                    case 10 -> {
                        IO.println("== Consulta 10: Estadísticas duración viajes");
                        servicio.getEstadisticasDuracion();
                    }
                    case 11 -> {
                        IO.println("== Consulta 11: Viajes gratuitos o de bajo coste");
                        servicio.getViajesBaratos(1.50).forEach(System.out::println);

                    }
                    case 12 -> {
                        IO.println("== Consulta 12: Todos los viajeros con abono ANUAL superan 100 puntos");
                        boolean todosSuperan100 = servicio.todosAnualesSuperanPuntos(100);
                        if (todosSuperan100) {
                            IO.println("Todos los viajeros superan 100 puntos");
                        } else {
                            IO.println("No todos los viajeros superan 100 puntos");
                        }
                    }
                    case 13 -> {
                        IO.println("== Consulta 13: Mostrar gasto de los viajeros en un mes");
                        IO.println("Dime mes (1-12): ");
                        int mes = Integer.parseInt(IO.readln());
                        servicio.getGastoPorViajeroEnMes(mes).forEach((nombre, gasto) -> {
                            IO.println(nombre + " gastó en el mes " + Month.of(mes) + ": " + gasto + "€");
                        });
                    }
                    case 14 -> {
                        IO.println("== Consulta 14: Línea con más incidencias");
                        servicio.getLineaConMasIncidencias().ifPresent(linea -> {
                            IO.println("La línea con más incidencias es: " + linea);
                        });
                    }
                    case 15 -> {
                        IO.println("== Consulta 15: Viajeros con abono OCASIONAL y gasto alto");
                        IO.println("Dime umbral: ");
                        double umbral = Double.parseDouble(IO.readln());
                        servicio.getOcasionalesGastoAlto(umbral).forEach(System.out::println);

                    }
                    case 16 -> IO.println("== Salir");
                    default -> IO.println("== OPCIÓN INCORRECTA");
                }

            } catch (Exception ex) {
                log.error("Error de entrada/salida {}", ex.getMessage());
            }
        } while (opcion != 16);

    }
}