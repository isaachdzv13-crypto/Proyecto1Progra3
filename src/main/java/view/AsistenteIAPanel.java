package view;

import Theme.Themes;

import javax.swing.*;
import java.awt.*;

public class AsistenteIAPanel extends JPanel {

    public final JTextArea txtSolicitud = new JTextArea(4, 40);
    public final JButton btnInterpretar = Themes.button("Interpretar con IA", Themes.PRIMARY);

    public final JLabel lblActividad = new JLabel("-");
    public final JLabel lblFecha = new JLabel("-");
    public final JLabel lblHorario = new JLabel("-");
    public final JLabel lblCategoria = new JLabel("-");

    public final JButton btnUsarEnFormulario = Themes.button("Usar en formulario de reserva", Themes.SUCCESS);

    public AsistenteIAPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel entrada = new JPanel(new BorderLayout(8, 8));
        entrada.setBorder(BorderFactory.createTitledBorder("Describa la reserva que necesita"));

        txtSolicitud.setLineWrap(true);
        txtSolicitud.setWrapStyleWord(true);
        entrada.add(new JScrollPane(txtSolicitud), BorderLayout.CENTER);

        JPanel botonInterpretar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonInterpretar.add(btnInterpretar);
        entrada.add(botonInterpretar, BorderLayout.SOUTH);

        JPanel resultado = new JPanel(new GridLayout(0, 2, 12, 8));
        resultado.setBorder(BorderFactory.createTitledBorder("La IA entendio esto"));

        resultado.add(new JLabel("Actividad:"));
        resultado.add(lblActividad);
        resultado.add(new JLabel("Fecha:"));
        resultado.add(lblFecha);
        resultado.add(new JLabel("Horario:"));
        resultado.add(lblHorario);
        resultado.add(new JLabel("Categoria de recurso:"));
        resultado.add(lblCategoria);

        JPanel botonUsar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonUsar.add(btnUsarEnFormulario);
        btnUsarEnFormulario.setEnabled(false);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(entrada);
        centro.add(Box.createVerticalStrut(10));
        centro.add(resultado);
        centro.add(botonUsar);

        add(centro, BorderLayout.NORTH);
    }
}
