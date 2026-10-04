package dao;

import modelo.Recompensa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class CanjeDAO {

    /**
     * Obtiene todas las recompensas disponibles.
     */
    public List<Recompensa> listarRecompensas() {

        List<Recompensa> lista = new ArrayList<>();

        String sql =
                "SELECT id_recompensa, "
                + "nombre_item, costo_puntos "
                + "FROM recompensas "
                + "ORDER BY id_recompensa";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement pstmt =
                        conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ) {

            if (conn == null) {
                return lista;
            }

            while (rs.next()) {

                lista.add(
                        new Recompensa(
                                rs.getInt("id_recompensa"),
                                rs.getString("nombre_item"),
                                rs.getInt("costo_puntos")
                        )
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar recompensas: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    /**
     * Realiza el canje completo:
     *
     * 1. Comprueba puntos.
     * 2. Descuenta puntos.
     * 3. Registra el canje.
     *
     * Todo dentro de una transacción.
     */
    public CanjeResultado realizarCanje(
            int idEstudiante,
            int idRecompensa) {

        String sqlRecompensa =
                "SELECT nombre_item, costo_puntos "
                + "FROM recompensas "
                + "WHERE id_recompensa = ?";

        String sqlEstudiante =
                "SELECT puntos "
                + "FROM estudiantes "
                + "WHERE id_estudiante = ? "
                + "FOR UPDATE";

        String sqlActualizar =
                "UPDATE estudiantes "
                + "SET puntos = puntos - ? "
                + "WHERE id_estudiante = ?";

        String sqlHistorial =
                "INSERT INTO historial_canjes "
                + "(id_estudiante, id_recompensa) "
                + "VALUES (?, ?)";

        Connection conn = null;

        try {

            conn = ConexionBD.obtenerConexion();

            if (conn == null) {
                return CanjeResultado.error(
                        "No se pudo conectar con MySQL."
                );
            }

            // Comenzamos transacción
            conn.setAutoCommit(false);

            String nombreItem;
            int costo;

            // 1. Obtener recompensa
            try (
                    PreparedStatement pstmt =
                            conn.prepareStatement(
                                    sqlRecompensa
                            )
            ) {

                pstmt.setInt(1, idRecompensa);

                try (ResultSet rs =
                             pstmt.executeQuery()) {

                    if (!rs.next()) {

                        conn.rollback();

                        return CanjeResultado.error(
                                "La recompensa no existe."
                        );
                    }

                    nombreItem =
                            rs.getString("nombre_item");

                    costo =
                            rs.getInt("costo_puntos");
                }
            }

            int puntosActuales;

            // 2. Obtener puntos del estudiante
            try (
                    PreparedStatement pstmt =
                            conn.prepareStatement(
                                    sqlEstudiante
                            )
            ) {

                pstmt.setInt(1, idEstudiante);

                try (ResultSet rs =
                             pstmt.executeQuery()) {

                    if (!rs.next()) {

                        conn.rollback();

                        return CanjeResultado.error(
                                "El estudiante no existe."
                        );
                    }

                    puntosActuales =
                            rs.getInt("puntos");
                }
            }

            // 3. Comprobar puntos
            if (puntosActuales < costo) {

                conn.rollback();

                return CanjeResultado.error(
                        "No tienes suficientes puntos."
                );
            }

            int puntosRestantes =
                    puntosActuales - costo;

            // 4. Actualizar puntos
            try (
                    PreparedStatement pstmt =
                            conn.prepareStatement(
                                    sqlActualizar
                            )
            ) {

                pstmt.setInt(1, costo);
                pstmt.setInt(2, idEstudiante);

                pstmt.executeUpdate();
            }

            // 5. Registrar historial
            try (
                    PreparedStatement pstmt =
                            conn.prepareStatement(
                                    sqlHistorial
                            )
            ) {

                pstmt.setInt(1, idEstudiante);
                pstmt.setInt(2, idRecompensa);

                pstmt.executeUpdate();
            }

            // 6. Confirmar
            conn.commit();

            return CanjeResultado.exitoso(
                    nombreItem,
                    costo,
                    puntosRestantes
            );

        } catch (SQLException e) {

            if (conn != null) {

                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }

            return CanjeResultado.error(
                    "Error durante el canje: "
                    + e.getMessage()
            );

        } finally {

            if (conn != null) {

                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Clase auxiliar para devolver el resultado del canje.
     */
    public static class CanjeResultado {

        private boolean exitoso;
        private String mensaje;
        private String nombreItem;
        private int costo;
        private int puntosRestantes;

        private CanjeResultado(
                boolean exitoso,
                String mensaje,
                String nombreItem,
                int costo,
                int puntosRestantes) {

            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.nombreItem = nombreItem;
            this.costo = costo;
            this.puntosRestantes = puntosRestantes;
        }

        public static CanjeResultado exitoso(
                String nombreItem,
                int costo,
                int puntosRestantes) {

            return new CanjeResultado(
                    true,
                    "Canje realizado correctamente.",
                    nombreItem,
                    costo,
                    puntosRestantes
            );
        }

        public static CanjeResultado error(
                String mensaje) {

            return new CanjeResultado(
                    false,
                    mensaje,
                    "",
                    0,
                    0
            );
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public String getNombreItem() {
            return nombreItem;
        }

        public int getCosto() {
            return costo;
        }

        public int getPuntosRestantes() {
            return puntosRestantes;
        }
    }
}
