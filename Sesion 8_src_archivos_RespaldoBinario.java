package archivos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RespaldoBinario {

    private static final String ARCHIVO =
            "config_respaldo.dat";

    public static void guardarRespaldoConfig(
            String configuracion) {

        try (
                ObjectOutputStream oos =
                        new ObjectOutputStream(
                                new FileOutputStream(
                                        ARCHIVO
                                )
                        )
        ) {

            oos.writeObject(configuracion);

            System.out.println(
                    "Respaldo binario generado correctamente."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al guardar respaldo: "
                    + e.getMessage()
            );
        }
    }

    public static String leerRespaldoConfig() {

        try (
                ObjectInputStream ois =
                        new ObjectInputStream(
                                new FileInputStream(
                                        ARCHIVO
                                )
                        )
        ) {

            return (String) ois.readObject();

        } catch (
                IOException
                | ClassNotFoundException e
        ) {

            return "No existe un respaldo todavía.";
        }
    }
}
