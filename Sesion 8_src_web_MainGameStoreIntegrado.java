package web;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class MainGameStoreIntegrado {

    public static void main(String[] args)
            throws Exception {

        int puerto = 8080;

        HttpServer servidor =
                HttpServer.create(
                        new InetSocketAddress(
                                puerto
                        ),
                        0
                );

        GameStoreLoginServletIntegrado handler =
                new GameStoreLoginServletIntegrado();

        servidor.createContext(
                "/login",
                handler
        );

        servidor.createContext(
                "/catalogo",
                handler
        );

        servidor.createContext(
                "/canjear",
                handler
        );

        servidor.createContext(
                "/historial",
                handler
        );

        servidor.createContext(
                "/logout",
                handler
        );

        servidor.setExecutor(null);

        servidor.start();

        System.out.println(
                "===================================="
        );

        System.out.println(
                "      GAMESTORE INICIADO"
        );

        System.out.println(
                "===================================="
        );

        System.out.println(
                "Servidor listo: "
                + "http://localhost:"
                + puerto
                + "/login"
        );

        System.out.println(
                "Presiona Ctrl+C para detenerlo."
        );
    }
}
