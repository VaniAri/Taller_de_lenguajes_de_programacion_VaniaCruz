package modelo;

/**
 * Representa un ítem de avatar dentro de GameStore.
 * Hereda de ItemTienda y demuestra polimorfismo.
 */
public class ItemAvatar extends ItemTienda {

    public ItemAvatar(String nombre, int costo) {
        super(nombre, costo);
    }

    @Override
    public void aplicarEfectoEspecial() {
        System.out.println(
                "Ítem: " + getNombre()
                + " | Costo: " + getCosto() + " pts"
        );

        System.out.println(
                "Aplicando personalización especial al avatar."
        );
    }
}
