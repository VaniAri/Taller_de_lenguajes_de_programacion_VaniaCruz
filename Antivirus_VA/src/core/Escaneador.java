package core;
 
import database.BaseFirmas;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.stream.Stream;
 
public class Escaneador {
 
    private final BaseFirmas baseFirmas;
    private final Cuarentena cuarentena;
 
    public Escaneador(BaseFirmas baseFirmas, Cuarentena cuarentena) {
        this.baseFirmas = baseFirmas;
        this.cuarentena = cuarentena;
    }
 
    public boolean analizarArchivo(Path ruta) {
        String hash = calcularSha256(ruta);
        return baseFirmas.esHashMalicioso(hash);
    }
 
    public Resultado analizarCarpeta(Path ruta) {
        Resultado resultado = new Resultado();
        if (Files.isRegularFile(ruta)) {
            analizarYRegistrar(ruta, resultado);
            return resultado;
        }
        try (Stream<Path> archivos = Files.walk(ruta)) {
            archivos.filter(Files::isRegularFile)
                    .forEach(archivo -> analizarYRegistrar(archivo, resultado));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo recorrer la carpeta: " + e.getMessage(), e);
        }
        return resultado;
    }
 
    private void analizarYRegistrar(Path archivo, Resultado resultado) {
        resultado.registrarArchivoAnalizado();
        if (analizarArchivo(archivo)) {
            resultado.registrarArchivoMalicioso(archivo.toString());
            cuarentena.aislarArchivo(archivo);
        }
    }
 
    private String calcularSha256(Path ruta) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] contenido = Files.readAllBytes(ruta);
            byte[] hash = digest.digest(contenido);
            return HexFormat.of().formatHex(hash);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("No se pudo calcular el hash de " + ruta + ": " + e.getMessage(), e);
        }
    }
}
