package database;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
 
public class BaseFirmas {
 
    private final Path rutaBaseDatos;
    private final Set<String> hashesMaliciosos = new HashSet<>();
 
    public BaseFirmas(Path rutaBaseDatos) {
        this.rutaBaseDatos = rutaBaseDatos;
        cargarFirmas();
    }
 
    public void cargarFirmas() {
        hashesMaliciosos.clear();
        if (!Files.exists(rutaBaseDatos)) {
            return;
        }
        try {
            for (String linea : Files.readAllLines(rutaBaseDatos)) {
                String hash = linea.trim();
                if (hash.isEmpty() || hash.startsWith("#")) {
                    continue;
                }
                hashesMaliciosos.add(hash.toLowerCase());
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer firmas.db: " + e.getMessage(), e);
        }
    }
 
    public boolean esHashMalicioso(String hash) {
        return hashesMaliciosos.contains(hash.toLowerCase());
    }
 
    public int totalFirmas() {
        return hashesMaliciosos.size();
    }
}
