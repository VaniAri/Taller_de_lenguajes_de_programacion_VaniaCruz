package modelo;

public class Estudiante {

    private int idEstudiante;
    private String nombre;
    private int puntos;

    public Estudiante(int idEstudiante, String nombre, int puntos) {
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.puntos = puntos;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "idEstudiante=" + idEstudiante +
                ", nombre='" + nombre + '\'' +
                ", puntos=" + puntos +
                '}';
    }
}
