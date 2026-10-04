package modelo;

/**
 * Clase abstracta base para los elementos de GameStore.
 * Demuestra encapsulamiento y polimorfismo.
 */
public abstract class ItemTienda {

    private String nombre;
    private int costo;

    public ItemTienda(String nombre, int costo) {
        this.nombre = nombre;
        this.costo = costo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCosto() {
        return costo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    @Override
    public String toString() {
        return "ItemTienda{" +
                "nombre='" + nombre + '\'' +
                ", costo=" + costo +
                '}';
    }

    public abstract void aplicarEfectoEspecial();
}
