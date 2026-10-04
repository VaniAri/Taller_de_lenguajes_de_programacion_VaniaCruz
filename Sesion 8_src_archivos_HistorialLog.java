package archivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HistorialLog {

    private static final String ARCHIVO =
            "historial_canjes.txt";

    public static void registrarCanje(
            String estudiante,
            String itemCanjeado,
            int costo,
            int puntosRestantes) {

        try (
                PrintWriter pw =
                        new PrintWriter(
                                new FileWriter(
                                        ARCHIVO,
                                        true
                                )
                        )
        ) {

            pw.println(
                    "Estudiante: " + estudiante
                    + " | Ítem: " + itemCanjeado
                    + " | Costo: " + costo + " pts"
                    + " | Puntos restantes: "
                    + puntosRestantes
            );

            System.out.println(
                    "Registro guardado en "
                    + ARCHIVO
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al escribir historial: "
                    + e.getMessage()
            );
        }
    }

    public static String obtenerHistorial() {

        StringBuilder texto =
                new StringBuilder();

        try (
                BufferedReader br =
                        new BufferedReader(
                                new FileReader(ARCHIVO)
                        )
        ) {

            String linea;

            while ((linea = br.readLine()) != null) {

                texto.append(linea)
                        .append("\n");
            }

        } catch (IOException e) {

            texto.append(
                    "Todavía no existe un historial."
            );
        }

        return texto.toString();
    }
}
