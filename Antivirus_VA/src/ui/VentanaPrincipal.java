package ui;

import core.Cuarentena;
import core.Escaneador;
import core.Resultado;
import database.BaseFirmas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VentanaPrincipal extends JFrame {

    private final JTextArea areaResultado = new JTextArea();
    private final JLabel etiquetaEstado = new JLabel("Listo");
    private final Escaneador escaneador;

    public VentanaPrincipal() {
        super("Antivirus - VaniAri");

        Path raizProyecto = Path.of(System.getProperty("user.dir"));
        BaseFirmas baseFirmas = new BaseFirmas(raizProyecto.resolve("firmas.db"));
        Cuarentena cuarentena = new Cuarentena(raizProyecto.resolve("cuarentena"));
        this.escaneador = new Escaneador(baseFirmas, cuarentena);

        construirInterfaz();

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(255, 239, 247));
        JLabel titulo = new JLabel("Antivirus - VaniAri", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(new Color(172, 28, 112));
        panelSuperior.add(titulo, BorderLayout.NORTH);
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        controles.setOpaque(false);
        JButton botonEscanear = new JButton("Selecciona y escanea...");
        botonEscanear.addActionListener(e -> iniciarEscaneoManual());
        botonEscanear.setBackground(new Color(220, 58, 145));
        botonEscanear.setForeground(Color.WHITE);
        botonEscanear.setFocusPainted(false);
        botonEscanear.setOpaque(true);
        botonEscanear.setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 16, 9, 16));
        etiquetaEstado.setForeground(new Color(148, 37, 100));
        controles.add(botonEscanear);
        controles.add(etiquetaEstado);
        panelSuperior.add(controles, BorderLayout.CENTER);

        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(areaResultado), BorderLayout.CENTER);
    }

    public void iniciarEscaneoManual() {
        JFileChooser selector = new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int seleccion = selector.showOpenDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path ruta = selector.getSelectedFile().toPath();
        mostrarEstado("Escaneando...");
        Resultado resultado = escaneador.analizarCarpeta(ruta);
        mostrarResultado(resultado);
        mostrarEstado("Listo");
    }

    public void mostrarEstado(String estado) {
        etiquetaEstado.setText(estado);
    }

    public void mostrarResultado(Resultado resultado) {
        areaResultado.setText(resultado.toString());
    }
}
