package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/gamestore_db"
            + "?useSSL=false&serverTimezone=UTC";

    private static final String USUARIO = "root";

    // Si tu MySQL tiene contraseña, escríbela aquí.
    private static final String PASSWORD = "";

    public static Connection obtenerConexion() {

        try {

            Connection conexion =
                    DriverManager.getConnection(
                            URL,
                            USUARIO,
                            PASSWORD
                    );

            return conexion;

        } catch (SQLException e) {

            System.out.println(
                    "Error al conectar con MySQL: "
                    + e.getMessage()
            );

            return null;
        }
    }
}
