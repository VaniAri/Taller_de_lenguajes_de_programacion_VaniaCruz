package modelo;

public class Recompensa {

    private int idRecompensa;
    private String nombreItem;
    private int costoPuntos;

    public Recompensa(
            int idRecompensa,
            String nombreItem,
            int costoPuntos) {

        this.idRecompensa = idRecompensa;
        this.nombreItem = nombreItem;
        this.costoPuntos = costoPuntos;
    }

    public int getIdRecompensa() {
        return idRecompensa;
    }

    public String getNombreItem() {
        return nombreItem;
    }

    public int getCostoPuntos() {
        return costoPuntos;
    }

    @Override
    public String toString() {
        return "Recompensa{" +
                "id=" + idRecompensa +
                ", nombre='" + nombreItem + '\'' +
                ", costo=" + costoPuntos +
                '}';
    }
}
