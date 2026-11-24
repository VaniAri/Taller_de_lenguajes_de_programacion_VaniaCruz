package core;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
 
public class Cuarentena {
 
    private final Path carpetaCuarentena;
    private final Path manifiesto;
 
    public Cuarentena(Path carpetaCuarentena) {
        this.carpetaCuarentena = carpetaCuarentena;
        this.manifiesto = carpetaCuarentena.resolve("manifest.properties");
        try {
            Files.createDirectories(carpetaCuarentena);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear carpeta de cuarentena: " + e.getMessage(), e);
        }
    }
 
    public Path aislarArchivo(Path ruta) {
        try {
            String nombreDestino = System.currentTimeMillis() + "_" + ruta.getFileName();
            Path destino = carpetaCuarentena.resolve(nombreDestino);
            Files.move(ruta, destino, StandardCopyOption.REPLACE_EXISTING);
            guardarEnManifiesto(nombreDestino, ruta.toAbsolutePath().toString());
            return destino;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo aislar el archivo: " + e.getMessage(), e);
        }
    }
 
    public boolean restaurarArchivo(Path rutaOriginal) {
        Properties manifest = cargarManifiesto();
        String claveOriginal = rutaOriginal.toAbsolutePath().toString();
        for (String nombreCuarentena : manifest.stringPropertyNames()) {
            if (manifest.getProperty(nombreCuarentena).equals(claveOriginal)) {
                try {
                    Path origen = carpetaCuarentena.resolve(nombreCuarentena);
                    Files.move(origen, rutaOriginal, StandardCopyOption.REPLACE_EXISTING);
                    manifest.remove(nombreCuarentena);
                    guardarManifiestoCompleto(manifest);
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException("No se pudo restaurar el archivo: " + e.getMessage(), e);
                }
            }
        }
        return false;
    }
 
    private void guardarEnManifiesto(String nombreCuarentena, String rutaOriginal) {
        Properties manifest = cargarManifiesto();
        manifest.setProperty(nombreCuarentena, rutaOriginal);
        guardarManifiestoCompleto(manifest);
    }
 
    private Properties cargarManifiesto() {
        Properties props = new Properties();
        if (Files.exists(manifiesto)) {
            try (InputStream in = Files.newInputStream(manifiesto)) {
                props.load(in);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo leer manifest.properties: " + e.getMessage(), e);
            }
        }
        return props;
    }
 
    private void guardarManifiestoCompleto(Properties props) {
        try (OutputStream out = Files.newOutputStream(manifiesto)) {
            props.store(out, "Manifiesto de cuarentena - Antivirus Java");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir manifest.properties: " + e.getMessage(), e);
        }
    }
}
