package dao;

import modelo.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstudianteDAO {

    /**
     * Busca un estudiante por nombre.
     */
    public Estudiante buscarPorNombre(String nombre) {

        String sql =
                "SELECT id_estudiante, nombre, puntos "
                + "FROM estudiantes "
                + "WHERE LOWER(TRIM(nombre)) = LOWER(TRIM(?))";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement pstmt =
                        conn.prepareStatement(sql)
        ) {

            if (conn == null) {
                return null;
            }

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    return new Estudiante(
                            rs.getInt("id_estudiante"),
                            rs.getString("nombre"),
                            rs.getInt("puntos")
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar estudiante: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Método original de la sesión JDBC.
     */
    public void insertarEstudiante(
            String nombre,
            int puntos) {

        String sql =
                "INSERT INTO estudiantes "
                + "(nombre, puntos) VALUES (?, ?)";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement pstmt =
                        conn.prepareStatement(sql)
        ) {

            if (conn == null) {
                return;
            }

            pstmt.setString(1, nombre);
            pstmt.setInt(2, puntos);

            pstmt.executeUpdate();

            System.out.println(
                    "Estudiante registrado correctamente."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar estudiante: "
                    + e.getMessage()
            );
        }
    }

    /**
     * Método original de consulta de estudiantes.
     */
    public void listarEstudiantes() {

        String sql =
                "SELECT id_estudiante, nombre, puntos "
                + "FROM estudiantes";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement pstmt =
                        conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ) {

            if (conn == null) {
                return;
            }

            System.out.println(
                    "--- ESTUDIANTES DESDE MYSQL ---"
            );

            while (rs.next()) {

                System.out.println(
                        "ID: "
                        + rs.getInt("id_estudiante")
                        + " | Nombre: "
                        + rs.getString("nombre")
                        + " | Puntos: "
                        + rs.getInt("puntos")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al consultar estudiantes: "
                    + e.getMessage()
            );
        }
    }
}
