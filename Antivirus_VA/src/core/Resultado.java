package core;
 
import java.util.ArrayList;
import java.util.List;
 
public class Resultado {
 
    private int archivosAnalizados = 0;
    private final List<String> archivosMaliciosos = new ArrayList<>();
 
    public void registrarArchivoAnalizado() {
        archivosAnalizados++;
    }
 
    public void registrarArchivoMalicioso(String ruta) {
        archivosMaliciosos.add(ruta);
    }
 
    public int getArchivosAnalizados() {
        return archivosAnalizados;
    }
 
    public List<String> getArchivosMaliciosos() {
        return archivosMaliciosos;
    }
 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Archivos analizados: ").append(archivosAnalizados).append("\n");
        sb.append("Amenazas detectadas: ").append(archivosMaliciosos.size()).append("\n");
        for (String ruta : archivosMaliciosos) {
            sb.append("  - ").append(ruta).append(" -> aislado en cuarentena\n");
        }
        return sb.toString();
    }
}
