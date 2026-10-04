package web;

import archivos.HistorialLog;
import archivos.RespaldoBinario;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CanjeDAO;
import dao.EstudianteDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Estudiante;
import modelo.Recompensa;

public class GameStoreLoginServletIntegrado
        implements HttpHandler {

    private final EstudianteDAO estudianteDAO;
    private final CanjeDAO canjeDAO;

    public GameStoreLoginServletIntegrado() {

        estudianteDAO = new EstudianteDAO();
        canjeDAO = new CanjeDAO();
    }

    @Override
    public void handle(HttpExchange exchange)
            throws IOException {

        String ruta =
                exchange.getRequestURI().getPath();

        String metodo =
                exchange.getRequestMethod();

        try {

            if (ruta.equals("/login")) {

                manejarLogin(exchange, metodo);

            } else if (ruta.equals("/catalogo")) {

                manejarCatalogo(exchange);

            } else if (ruta.equals("/canjear")) {

                manejarCanje(exchange, metodo);

            } else if (ruta.equals("/historial")) {

                manejarHistorial(exchange);

            } else if (ruta.equals("/logout")) {

                manejarLogout(exchange);

            } else {

                responder(
                        exchange,
                        404,
                        paginaError(
                                "Página no encontrada."
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            responder(
                    exchange,
                    500,
                    paginaError(
                            "Ocurrió un error interno: "
                            + escapar(
                                    e.getMessage()
                            )
                    )
            );
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    private void manejarLogin(
            HttpExchange exchange,
            String metodo)
            throws IOException {

        if (metodo.equalsIgnoreCase("GET")) {

            responder(
                    exchange,
                    200,
                    paginaLogin("")
            );

            return;
        }

        if (metodo.equalsIgnoreCase("POST")) {

            String cuerpo =
                    leerCuerpo(exchange);

            Map<String, String> datos =
                    parsearFormulario(cuerpo);

            String nombre =
                    datos.getOrDefault(
                            "nombre",
                            ""
                    ).trim();

            if (nombre.isEmpty()) {

                responder(
                        exchange,
                        400,
                        paginaLogin(
                                "Ingresa tu nombre."
                        )
                );

                return;
            }

            Estudiante estudiante =
                    estudianteDAO.buscarPorNombre(
                            nombre
                    );

            if (estudiante == null) {

                responder(
                        exchange,
                        401,
                        paginaLogin(
                                "Error: usuario no encontrado "
                                + "en la base de datos."
                        )
                );

                return;
            }

            exchange.getResponseHeaders()
                    .add(
                            "Set-Cookie",
                            "GAMESTORE_USER_ID="
                            + estudiante.getIdEstudiante()
                            + "; Path=/"
                    );

            exchange.getResponseHeaders()
                    .add(
                            "Location",
                            "/catalogo"
                    );

            exchange.sendResponseHeaders(
                    302,
                    -1
            );

            exchange.close();
        }
    }

    // =====================================================
    // CATÁLOGO
    // =====================================================

    private void manejarCatalogo(
            HttpExchange exchange)
            throws IOException {

        Estudiante estudiante =
                obtenerEstudianteActual(exchange);

        if (estudiante == null) {

            redireccionar(
                    exchange,
                    "/login"
            );

            return;
        }

        List<Recompensa> recompensas =
                canjeDAO.listarRecompensas();

        StringBuilder html =
                new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html lang="es">

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width,
                          initial-scale=1.0">

                    <title>
                        GameStore - Catálogo Avanzado
                    </title>

                    <style>

                        * {
                            box-sizing: border-box;
                        }

                        body {
                            margin: 0;
                            font-family: Arial, sans-serif;

                            background:
                                linear-gradient(
                                    135deg,
                                    #171717,
                                    #241217,
                                    #171717
                                );

                            color: #eeeeee;
                        }

                        header {
                            background:
                                linear-gradient(
                                    135deg,
                                    #50101b,
                                    #821928
                                );

                            padding: 28px;
                            text-align: center;

                            border-bottom:
                                2px solid #b4283c;

                            box-shadow:
                                0 5px 20px
                                rgba(0,0,0,.4);
                        }

                        header h1 {
                            margin: 0;
                            font-size: 34px;
                        }

                        header p {
                            margin-bottom: 0;
                            color: #dddddd;
                        }

                        .contenedor {
                            width: 92%;
                            max-width: 1100px;
                            margin: 25px auto;
                        }

                        /* =========================
                           USUARIO
                           ========================= */

                        .usuario {
                            background: #292929;
                            border: 1px solid #681a28;
                            padding: 22px;
                            border-radius: 15px;
                            margin-bottom: 20px;

                            box-shadow:
                                0 8px 25px
                                rgba(0,0,0,.3);
                        }

                        .usuario h2 {
                            margin-top: 0;
                        }

                        .puntos {
                            color: #50c878;
                            font-size: 27px;
                            font-weight: bold;
                        }

                        /* =========================
                           OPCIONES
                           ========================= */

                        .opciones {
                            background: #50101b;
                            padding: 18px;
                            border-radius: 15px;
                            margin-bottom: 25px;

                            border:
                                1px solid #a52235;
                        }

                        .opciones-titulo {
                            font-size: 18px;
                            font-weight: bold;
                            margin-bottom: 12px;
                        }

                        .opciones-contenido {
                            display: flex;
                            flex-wrap: wrap;
                            align-items: center;
                            gap: 15px;
                        }

                        select {
                            background: #292929;
                            color: white;

                            border:
                                1px solid #a52235;

                            padding: 10px;
                            border-radius: 8px;
                        }

                        label {
                            cursor: pointer;
                        }

                        /* =========================
                           TÍTULO
                           ========================= */

                        .titulo-catalogo {
                            text-align: center;
                            margin-bottom: 20px;
                        }

                        .titulo-catalogo h2 {
                            font-size: 28px;
                        }

                        /* =========================
                           CATÁLOGO
                           ========================= */

                        .catalogo {
                            display: grid;

                            grid-template-columns:
                                repeat(
                                    auto-fit,
                                    minmax(240px, 1fr)
                                );

                            gap: 20px;
                        }

                        .card {
                            background: #292929;

                            border:
                                1px solid #681a28;

                            border-radius: 18px;
                            padding: 22px;

                            transition: .2s;

                            box-shadow:
                                0 8px 20px
                                rgba(0,0,0,.25);
                        }

                        .card:hover {
                            transform:
                                translateY(-5px);

                            border-color: #b4283c;

                            box-shadow:
                                0 12px 28px
                                rgba(130,25,40,.35);
                        }

                        .icono {
                            height: 100px;

                            display: flex;
                            align-items: center;
                            justify-content: center;

                            font-size: 52px;

                            background:
                                linear-gradient(
                                    135deg,
                                    #3a3a3a,
                                    #50101b
                                );

                            border-radius: 12px;
                            margin-bottom: 15px;
                        }

                        .card h2 {
                            margin-top: 0;
                            font-size: 20px;
                        }

                        .rareza {
                            display: inline-block;

                            padding: 5px 9px;

                            border-radius: 20px;

                            background: #50101b;
                            color: #ffb6c1;

                            font-size: 12px;

                            margin-bottom: 10px;
                        }

                        .precio {
                            color: #50c878;
                            font-size: 22px;
                            font-weight: bold;
                        }

                        .categoria {
                            color: #aaaaaa;
                            font-size: 13px;
                            margin-bottom: 15px;
                        }

                        button {
                            background: #821928;
                            color: white;

                            border: none;

                            padding: 12px;

                            border-radius: 9px;

                            cursor: pointer;

                            width: 100%;

                            font-weight: bold;
                        }

                        button:hover {
                            background: #a52235;
                        }

                        button:disabled {
                            background: #555555;
                            cursor: not-allowed;
                        }

                        /* =========================
                           ZONA DE CANJE
                           ========================= */

                        .canje {
                            margin-top: 30px;

                            background: #292929;

                            border:
                                2px solid #821928;

                            border-radius: 15px;

                            padding: 25px;

                            text-align: center;
                        }

                        .canje h2 {
                            margin-top: 0;
                        }

                        .boton-canjear {
                            max-width: 300px;

                            margin: 15px auto 0;

                            font-size: 18px;

                            background: #b4283c;
                        }

                        .boton-canjear:hover {
                            background: #d8324b;
                        }

                        /* =========================
                           MENÚ
                           ========================= */

                        .menu {
                            display: flex;

                            justify-content: center;

                            flex-wrap: wrap;

                            gap: 20px;

                            margin: 30px 0;
                        }

                        .menu a {
                            color: white;

                            text-decoration: none;

                            background: #292929;

                            border:
                                1px solid #681a28;

                            padding: 12px 20px;

                            border-radius: 9px;
                        }

                        .menu a:hover {
                            background: #50101b;
                        }

                    </style>

                </head>

                <body>

                <header>

                    <h1>
                        🎮 GAMESTORE
                    </h1>

                    <p>
                        Catálogo Avanzado de Recompensas
                    </p>

                </header>

                <div class="contenedor">
                """);

        // =====================================================
        // USUARIO
        // =====================================================

        html.append("""
                <div class="usuario">

                    <h2>
                        👋 Bienvenido,
                """);

        html.append(
                escapar(
                        estudiante.getNombre()
                )
        );

        html.append("""
                    </h2>

                    <div class="puntos">
                        ⭐
                """);

        html.append(
                estudiante.getPuntos()
        );

        html.append("""
                        puntos disponibles
                    </div>

                </div>
                """);

        // =====================================================
        // OPCIONES
        // =====================================================

        html.append("""
                <div class="opciones">

                    <div class="opciones-titulo">
                        ⚙️ ZONA DE OPCIONES
                    </div>

                    <div class="opciones-contenido">

                        <label>
                            Filtrar:
                        </label>

                        <select id="filtro"
                                onchange="filtrarCatalogo()">

                            <option value="todas">
                                Todas las categorías
                            </option>

                            <option value="avatar">
                                Avatars
                            </option>

                            <option value="pase">
                                Pases
                            </option>

                            <option value="accesorio">
                                Accesorios
                            </option>

                        </select>

                        <label>

                            <input
                                type="checkbox"
                                id="alertas">

                            🔔 Recibir alerta

                        </label>

                        <label>

                            <input
                                type="radio"
                                name="entrega"
                                value="Digital"
                                checked>

                            Digital

                        </label>

                        <label>

                            <input
                                type="radio"
                                name="entrega"
                                value="Físico">

                            Físico

                        </label>

                    </div>

                </div>

                <div class="titulo-catalogo">

                    <h2>
                        🛒 Catálogo de recompensas
                    </h2>

                    <p>
                        Selecciona un producto para
                        consultar sus detalles y canjearlo.
                    </p>

                </div>

                <div class="catalogo"
                     id="catalogo">
                """);

        // =====================================================
        // PRODUCTOS
        // =====================================================

        for (Recompensa recompensa : recompensas) {

            String nombre =
                    recompensa.getNombreItem();

            String categoria =
                    obtenerCategoria(nombre);

            String rareza =
                    obtenerRareza(nombre);

            String icono =
                    obtenerIcono(nombre);

            html.append(
                    "<div class='card producto' "
                    + "data-categoria='"
                    + categoria
                    + "' "
                    + "data-id='"
                    + recompensa.getIdRecompensa()
                    + "'>"
            );

            html.append(
                    "<div class='icono'>"
                    + icono
                    + "</div>"
            );

            html.append(
                    "<div class='rareza'>"
                    + escapar(rareza)
                    + "</div>"
            );

            html.append(
                    "<h2>"
                    + escapar(nombre)
                    + "</h2>"
            );

            html.append(
                    "<div class='categoria'>"
                    + "Categoría: "
                    + obtenerNombreCategoria(
                            categoria
                    )
                    + "</div>"
            );

            html.append(
                    "<div class='precio'>"
                    + recompensa.getCostoPuntos()
                    + " pts"
                    + "</div>"
            );

            html.append("<br>");

            /*
             * IMPORTANTE:
             * Ya NO ponemos un formulario dentro
             * de cada tarjeta.
             *
             * Solo seleccionamos el producto.
             */

            html.append(
                    "<button "
                    + "type='button' "
                    + "onclick=\"seleccionarProducto("
                    + "this, "
                    + recompensa.getIdRecompensa()
                    + ", '"
                    + escapar(nombre)
                    + "', "
                    + recompensa.getCostoPuntos()
                    + ", '"
                    + escapar(rareza)
                    + "')\">"
                    + "🛒 SELECCIONAR"
                    + "</button>"
            );

            html.append(
                    "</div>"
            );
        }

        // =====================================================
        // CIERRE CATÁLOGO + FORMULARIO ÚNICO
        // =====================================================

        html.append("""
                </div>

                <!-- =========================
                     ZONA DE CANJE
                     ========================= -->

                <div class="canje"
                     id="zonaCanje">

                    <h2>
                        🛍️ Producto seleccionado
                    </h2>

                    <p id="productoSeleccionado">
                        Ningún producto seleccionado
                    </p>

                    <p id="detalleSeleccionado">
                        Selecciona un producto del catálogo.
                    </p>

                    <form
                        method="POST"
                        action="/canjear"
                        id="formularioCanje">

                        <input
                            type="hidden"
                            name="idRecompensa"
                            id="idRecompensa">

                        <button
                            class="boton-canjear"
                            id="btnCanjear"
                            type="submit"
                            disabled>

                            🛒 CONFIRMAR CANJE

                        </button>

                    </form>

                </div>

                <div class="menu">

                    <a href="/historial">
                        📜 Ver historial
                    </a>

                    <a href="/logout">
                        🚪 Cerrar sesión
                    </a>

                </div>

                </div>

                <script>

                    let tarjetaSeleccionada = null;


                    // =================================================
                    // SELECCIONAR PRODUCTO
                    // =================================================

                    function seleccionarProducto(
                        boton,
                        id,
                        nombre,
                        costo,
                        rareza
                    ) {

                        tarjetaSeleccionada =
                            boton.closest(".card");


                        // Guardar ID de la recompensa
                        document
                            .getElementById(
                                "idRecompensa"
                            )
                            .value = id;


                        // Quitar selección anterior
                        document
                            .querySelectorAll(".card")
                            .forEach(
                                function(card) {

                                    card.style.borderColor =
                                        "#681a28";

                                }
                            );


                        // Marcar producto actual
                        tarjetaSeleccionada.style.borderColor =
                            "#50c878";


                        // Mostrar nombre
                        document
                            .getElementById(
                                "productoSeleccionado"
                            )
                            .innerText =
                                "🛒 " + nombre;


                        // Mostrar detalles
                        document
                            .getElementById(
                                "detalleSeleccionado"
                            )
                            .innerText =
                                "Rareza: "
                                + rareza
                                + " | Costo: "
                                + costo
                                + " puntos";


                        // Activar botón
                        document
                            .getElementById(
                                "btnCanjear"
                            )
                            .disabled = false;
                    }


                    // =================================================
                    // CONFIRMAR ANTES DE ENVIAR
                    // =================================================

                    document
                        .getElementById(
                            "formularioCanje"
                        )
                        .addEventListener(
                            "submit",
                            function(event) {

                                const id =
                                    document
                                        .getElementById(
                                            "idRecompensa"
                                        )
                                        .value;


                                if (
                                    !id
                                    || id === ""
                                ) {

                                    event.preventDefault();

                                    alert(
                                        "Primero selecciona un producto."
                                    );

                                    return;
                                }


                                const alertas =
                                    document
                                        .getElementById(
                                            "alertas"
                                        )
                                        .checked;


                                const entrega =
                                    document
                                        .querySelector(
                                            'input[name="entrega"]:checked'
                                        )
                                        .value;


                                const producto =
                                    document
                                        .getElementById(
                                            "productoSeleccionado"
                                        )
                                        .innerText;


                                const confirmar =
                                    confirm(
                                        "=== CONFIRMACIÓN DE CANJE ===\\n\\n"
                                        + producto
                                        + "\\n"
                                        + "Entrega: "
                                        + entrega
                                        + "\\n"
                                        + "Alerta: "
                                        + (
                                            alertas
                                            ? "Activada"
                                            : "Desactivada"
                                        )
                                        + "\\n\\n"
                                        + "¿Deseas realizar el canje?"
                                    );


                                if (!confirmar) {

                                    event.preventDefault();

                                }

                            }
                        );


                    // =================================================
                    // FILTRO
                    // =================================================

                    function filtrarCatalogo() {

                        const filtro =
                            document
                                .getElementById(
                                    "filtro"
                                )
                                .value;


                        const productos =
                            document
                                .querySelectorAll(
                                    ".producto"
                                );


                        productos.forEach(
                            function(producto) {

                                const categoria =
                                    producto.dataset
                                        .categoria;


                                if (
                                    filtro === "todas"
                                    ||
                                    filtro === categoria
                                ) {

                                    producto.style.display =
                                        "block";

                                } else {

                                    producto.style.display =
                                        "none";
                                }

                            }
                        );
                    }

                </script>

                </body>

                </html>
                """);

        responder(
                exchange,
                200,
                html.toString()
        );
    }

    // =====================================================
    // CANJE
    // =====================================================

    private void manejarCanje(
            HttpExchange exchange,
            String metodo)
            throws IOException {

        if (!metodo.equalsIgnoreCase("POST")) {

            redireccionar(
                    exchange,
                    "/catalogo"
            );

            return;
        }

        Estudiante estudiante =
                obtenerEstudianteActual(exchange);

        if (estudiante == null) {

            redireccionar(
                    exchange,
                    "/login"
            );

            return;
        }

        String cuerpo =
                leerCuerpo(exchange);

        Map<String, String> datos =
                parsearFormulario(cuerpo);

        String idTexto =
                datos.get("idRecompensa");


        // =================================================
        // COMPROBAR QUE LLEGÓ EL ID
        // =================================================

        if (
                idTexto == null
                || idTexto.trim().isEmpty()
        ) {

            responder(
                    exchange,
                    400,
                    paginaError(
                            "No se recibió el ID de la recompensa."
                    )
            );

            return;
        }


        int idRecompensa;

        try {

            idRecompensa =
                    Integer.parseInt(
                            idTexto
                    );

        } catch (
                NumberFormatException e
        ) {

            responder(
                    exchange,
                    400,
                    paginaError(
                            "ID de recompensa inválido."
                    )
            );

            return;
        }


        // =================================================
        // REALIZAR CANJE
        // =================================================

        CanjeDAO.CanjeResultado resultado =
                canjeDAO.realizarCanje(
                        estudiante.getIdEstudiante(),
                        idRecompensa
                );


        // =================================================
        // CANJE CORRECTO
        // =================================================

        if (resultado.isExitoso()) {

            HistorialLog.registrarCanje(
                    estudiante.getNombre(),
                    resultado.getNombreItem(),
                    resultado.getCosto(),
                    resultado.getPuntosRestantes()
            );


            RespaldoBinario.guardarRespaldoConfig(
                    "Servidor=Activo;"
                    + "UltimoCanje="
                    + resultado.getNombreItem()
                    + ";Estudiante="
                    + estudiante.getNombre()
            );


            responder(
                    exchange,
                    200,
                    paginaExito(
                            estudiante.getNombre(),
                            resultado
                    )
            );

        } else {

            responder(
                    exchange,
                    400,
                    paginaError(
                            resultado.getMensaje()
                    )
            );
        }
    }

    // =====================================================
    // HISTORIAL
    // =====================================================

    private void manejarHistorial(
            HttpExchange exchange)
            throws IOException {

        Estudiante estudiante =
                obtenerEstudianteActual(exchange);

        if (estudiante == null) {

            redireccionar(
                    exchange,
                    "/login"
            );

            return;
        }

        String historial =
                HistorialLog.obtenerHistorial();


        String html =
                """
                <!DOCTYPE html>

                <html lang="es">

                <head>

                    <meta charset="UTF-8">

                    <title>
                        Historial - GameStore
                    </title>

                    <style>

                        body {
                            background: #171717;
                            color: white;
                            font-family: Arial;
                            padding: 40px;
                        }

                        .caja {
                            max-width: 900px;
                            margin: auto;
                            background: #292929;
                            padding: 30px;
                            border-radius: 15px;
                            border: 1px solid #681a28;
                        }

                        pre {
                            white-space: pre-wrap;
                            background: #111111;
                            padding: 20px;
                            border-radius: 10px;
                        }

                        a {
                            color: white;
                            text-decoration: none;
                            background: #821928;
                            padding: 10px 18px;
                            border-radius: 8px;
                        }

                    </style>

                </head>

                <body>

                    <div class="caja">

                        <h1>
                            📜 Historial de canjes
                        </h1>

                        <pre>
                """
                + escapar(historial)
                + """
                        </pre>

                        <br><br>

                        <a href="/catalogo">
                            ← Volver al catálogo
                        </a>

                    </div>

                </body>

                </html>
                """;


        responder(
                exchange,
                200,
                html
        );
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private void manejarLogout(
            HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders()
                .add(
                        "Set-Cookie",
                        "GAMESTORE_USER_ID=; "
                        + "Path=/; Max-Age=0"
                );

        redireccionar(
                exchange,
                "/login"
        );
    }

    // =====================================================
    // OBTENER USUARIO ACTUAL
    // =====================================================

    private Estudiante obtenerEstudianteActual(
            HttpExchange exchange) {

        String cookie =
                exchange.getRequestHeaders()
                        .getFirst("Cookie");

        if (cookie == null) {
            return null;
        }


        String[] cookies =
                cookie.split(";");


        for (String c : cookies) {

            String cookieLimpia =
                    c.trim();


            if (
                    cookieLimpia.startsWith(
                            "GAMESTORE_USER_ID="
                    )
            ) {

                String valor =
                        cookieLimpia.substring(
                                "GAMESTORE_USER_ID=".length()
                        );


                try {

                    int id =
                            Integer.parseInt(
                                    valor
                            );

                    return buscarPorId(id);

                } catch (
                        NumberFormatException e
                ) {

                    return null;
                }
            }
        }


        return null;
    }


    private Estudiante buscarPorId(
            int id) {

        String sql =
                "SELECT id_estudiante, nombre, puntos "
                + "FROM estudiantes "
                + "WHERE id_estudiante = ?";


        try (
                java.sql.Connection conn =
                        dao.ConexionBD.obtenerConexion();

                java.sql.PreparedStatement pstmt =
                        conn.prepareStatement(sql)
        ) {

            pstmt.setInt(
                    1,
                    id
            );


            try (
                    java.sql.ResultSet rs =
                            pstmt.executeQuery()
            ) {

                if (rs.next()) {

                    return new Estudiante(
                            rs.getInt(
                                    "id_estudiante"
                            ),

                            rs.getString(
                                    "nombre"
                            ),

                            rs.getInt(
                                    "puntos"
                            )
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error obteniendo usuario: "
                    + e.getMessage()
            );
        }


        return null;
    }

    // =====================================================
    // CATEGORÍAS
    // =====================================================

    private String obtenerCategoria(
            String nombre) {

        String texto =
                nombre.toLowerCase();


        if (
                texto.contains("skin")
                || texto.contains("avatar")
        ) {

            return "avatar";
        }


        if (
                texto.contains("pase")
        ) {

            return "pase";
        }


        return "accesorio";
    }


    private String obtenerNombreCategoria(
            String categoria) {

        switch (categoria) {

            case "avatar":
                return "Avatar";

            case "pase":
                return "Pase";

            default:
                return "Accesorio";
        }
    }

    // =====================================================
    // RAREZAS
    // =====================================================

    private String obtenerRareza(
            String nombre) {

        switch (nombre) {

            case "Skin Guerrero Cyber":
                return "Nivel 4: Legendario";

            case "Pase de Prórroga":
                return "Nivel 3: Ultra Raro";

            case "Insignia Dorada":
                return "Nivel 2: Raro";

            case "Cursor Personalizado":
                return "Nivel 1: Normal";

            case "Fondo Exclusivo":
                return "Nivel 1: Normal";

            case "Bonus Sorpresa":
                return "Nivel 4: Legendario";

            default:
                return "Nivel 1: Normal";
        }
    }

    // =====================================================
    // ICONOS
    // =====================================================

    private String obtenerIcono(
            String nombre) {

        String texto =
                nombre.toLowerCase();


        if (texto.contains("skin")) {
            return "🎮";
        }

        if (texto.contains("pase")) {
            return "🎟️";
        }

        if (texto.contains("cursor")) {
            return "🖱️";
        }

        if (texto.contains("insignia")) {
            return "🏅";
        }

        if (texto.contains("fondo")) {
            return "🖼️";
        }

        if (texto.contains("bonus")) {
            return "🎁";
        }


        return "⭐";
    }

    // =====================================================
    // LEER CUERPO
    // =====================================================

    private String leerCuerpo(
            HttpExchange exchange)
            throws IOException {

        InputStream input =
                exchange.getRequestBody();


        return new String(
                input.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    // =====================================================
    // PARSEAR FORMULARIO
    // =====================================================

    private Map<String, String> parsearFormulario(
            String cuerpo) {

        Map<String, String> mapa =
                new HashMap<>();


        if (
                cuerpo == null
                || cuerpo.isEmpty()
        ) {

            return mapa;
        }


        String[] pares =
                cuerpo.split("&");


        for (String par : pares) {

            String[] partes =
                    par.split("=", 2);


            if (partes.length == 2) {

                String clave =
                        URLDecoder.decode(
                                partes[0],
                                StandardCharsets.UTF_8
                        );


                String valor =
                        URLDecoder.decode(
                                partes[1],
                                StandardCharsets.UTF_8
                        );


                mapa.put(
                        clave,
                        valor
                );
            }
        }


        return mapa;
    }

    // =====================================================
    // RESPONDER
    // =====================================================

    private void responder(
            HttpExchange exchange,
            int codigo,
            String contenido)
            throws IOException {

        byte[] datos =
                contenido.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/html; charset=UTF-8"
                );


        exchange.sendResponseHeaders(
                codigo,
                datos.length
        );


        try (
                OutputStream salida =
                        exchange.getResponseBody()
        ) {

            salida.write(datos);
        }
    }

    // =====================================================
    // REDIRECCIONAR
    // =====================================================

    private void redireccionar(
            HttpExchange exchange,
            String destino)
            throws IOException {

        exchange.getResponseHeaders()
                .add(
                        "Location",
                        destino
                );


        exchange.sendResponseHeaders(
                302,
                -1
        );


        exchange.close();
    }

    // =====================================================
    // ESCAPAR HTML
    // =====================================================

    private String escapar(
            String texto) {

        if (texto == null) {
            return "";
        }


        return texto
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                )
                .replace(
                        "\"",
                        "&quot;"
                )
                .replace(
                        "'",
                        "&#39;"
                );
    }

    // =====================================================
    // PÁGINA LOGIN
    // =====================================================

    private String paginaLogin(
            String mensaje) {

        String alerta = "";


        if (!mensaje.isEmpty()) {

            alerta =
                    "<div style='background:#6d1827;"
                    + "padding:15px;"
                    + "border-radius:8px;"
                    + "margin-bottom:20px;'>"
                    + escapar(mensaje)
                    + "</div>";
        }


        return """
                <!DOCTYPE html>

                <html lang="es">

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width,
                          initial-scale=1.0">

                    <title>
                        GameStore - Login
                    </title>

                    <style>

                        body {
                            background:
                                linear-gradient(
                                    135deg,
                                    #171717,
                                    #241217,
                                    #171717
                                );

                            color: white;
                            font-family: Arial;
                        }

                        .login {
                            width: 350px;
                            max-width: 90%;
                            margin: 100px auto;

                            background: #292929;

                            padding: 35px;

                            border-radius: 15px;

                            text-align: center;

                            border:
                                1px solid #681a28;

                            box-shadow:
                                0 10px 30px
                                rgba(0,0,0,.4);
                        }

                        input[type="text"] {
                            width: 90%;

                            padding: 12px;

                            margin: 15px 0;

                            border-radius: 8px;

                            border: none;
                        }

                        button {
                            width: 95%;

                            padding: 12px;

                            background: #821928;

                            color: white;

                            border: none;

                            border-radius: 8px;

                            cursor: pointer;

                            font-weight: bold;
                        }

                        button:hover {
                            background: #a52235;
                        }

                    </style>

                </head>

                <body>

                    <div class="login">

                        <h1>
                            🎮 GAMESTORE
                        </h1>

                        <p>
                            Acceso al sistema
                        </p>

                        """
                + alerta
                + """

                        <form method="POST"
                              action="/login">

                            <input
                                type="text"
                                name="nombre"
                                placeholder=
                                "Nombre del estudiante"
                                required
                            >

                            <button type="submit">
                                INGRESAR
                            </button>

                        </form>

                    </div>

                </body>

                </html>
                """;
    }

    // =====================================================
    // PÁGINA DE ÉXITO
    // =====================================================

    private String paginaExito(
            String estudiante,
            CanjeDAO.CanjeResultado resultado) {

        return """
                <!DOCTYPE html>

                <html lang="es">

                <head>

                    <meta charset="UTF-8">

                    <title>
                        Canje exitoso
                    </title>

                    <style>

                        body {
                            background: #171717;

                            color: white;

                            font-family: Arial;

                            text-align: center;

                            padding: 60px;
                        }

                        .caja {
                            max-width: 500px;

                            margin: auto;

                            background: #292929;

                            padding: 35px;

                            border-radius: 15px;

                            border:
                                1px solid #681a28;
                        }

                        .ok {
                            color: #50c878;

                            font-size: 28px;

                            font-weight: bold;
                        }

                        a {
                            color: white;

                            text-decoration: none;

                            background: #821928;

                            padding: 10px 18px;

                            border-radius: 8px;
                        }

                    </style>

                </head>

                <body>

                    <div class="caja">

                        <div class="ok">
                            ✓ CANJE EXITOSO
                        </div>

                        <h2>
                """
                + escapar(
                        resultado.getNombreItem()
                )
                + """
                        </h2>

                        <p>
                            Estudiante:
                            """
                + escapar(estudiante)
                + """
                        </p>

                        <p>
                            Puntos utilizados:
                            """
                + resultado.getCosto()
                + """
                        </p>

                        <p>
                            Puntos restantes:
                            """
                + resultado.getPuntosRestantes()
                + """
                        </p>

                        <br>

                        <a href="/catalogo">
                            ← Volver al catálogo
                        </a>

                    </div>

                </body>

                </html>
                """;
    }

    // =====================================================
    // PÁGINA ERROR
    // =====================================================

    private String paginaError(
            String mensaje) {

        return """
                <!DOCTYPE html>

                <html lang="es">

                <head>

                    <meta charset="UTF-8">

                    <title>
                        Error - GameStore
                    </title>

                </head>

                <body style="
                    background:#171717;
                    color:white;
                    font-family:Arial;
                    text-align:center;
                    padding:60px;
                ">

                    <h1>
                        ⚠️ GameStore
                    </h1>

                    <h2>
                """
                + escapar(mensaje)
                + """
                    </h2>

                    <br>

                    <a
                        href="/catalogo"
                        style="color:white;">
                        Volver
                    </a>

                </body>

                </html>
                """;
    }
}
