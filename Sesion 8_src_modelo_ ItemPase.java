package modelo;

public class ItemPase extends ItemTienda {

    private int nivelRareza;

    public ItemPase(String nombre, int costo, int nivelRareza) {
        super(nombre, costo);
        this.nivelRareza = nivelRareza;
    }

    public int getNivelRareza() {
        return nivelRareza;
    }

    public void setNivelRareza(int nivelRareza) {
        this.nivelRareza = nivelRareza;
    }

    public String getNombreRareza() {

        switch (nivelRareza) {
            case 1:
                return "Nivel 1: Normal";

            case 2:
                return "Nivel 2: Raro";

            case 3:
                return "Nivel 3: Ultra Raro";

            case 4:
                return "Nivel 4: Legendario";

            default:
                return "Nivel 1: Normal";
        }
    }

    @Override
    public void aplicarEfectoEspecial() {

        System.out.println(
                "Ítem: " + getNombre()
                + " | Costo: " + getCosto() + " pts"
        );

        System.out.println(
                "Desbloqueando skin de avatar tipo: "
                + getNombreRareza().split(": ")[1]
        );
    }
}
