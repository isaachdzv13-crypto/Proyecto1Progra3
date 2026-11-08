package controllers;

import contexto.Sesion;
import model.Recurso;
import model.Reserva;
import red.Notificacion;
import view.AlertasPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AlertasController {

    private final AlertasPanel vista;
    private final List<Notificacion> alertas = new ArrayList<>();

    public AlertasController(AlertasPanel vista) {
        this.vista = vista;

        vista.tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = vista.tabla.rowAtPoint(e.getPoint());
                int columna = vista.tabla.columnAtPoint(e.getPoint());
                if (fila < 0) return;

                if (columna == 1) {
                    verAlerta(fila);
                } else if (columna == 2) {
                    descartarAlerta(fila);
                }
            }
        });

        Sesion.setOyenteNotificaciones(n -> SwingUtilities.invokeLater(() -> agregarAlerta(n)));
    }

    public void cerrar() {
        Sesion.setOyenteNotificaciones(null);
    }

    private void agregarAlerta(Notificacion notificacion) {
        alertas.add(notificacion);
        String texto = notificacion.getNombreFuncionario() + " - " + notificacion.getReserva().getActividad();
        vista.modeloTabla.addRow(new Object[]{texto, "", ""});
    }

    private void verAlerta(int fila) {
        Notificacion notificacion = alertas.get(fila);
        Reserva reserva = notificacion.getReserva();

        DefaultListModel<String> modeloRecursos = new DefaultListModel<>();
        for (Recurso recurso : reserva.getRecursos()) {
            modeloRecursos.addElement(recurso.getDescripcion());
        }
        JList<String> listaRecursos = new JList<>(modeloRecursos);
        JScrollPane scroll = new JScrollPane(listaRecursos);
        scroll.setPreferredSize(new Dimension(220, 90));

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Encargado:"));
        panel.add(new JLabel(notificacion.getNombreFuncionario()));
        panel.add(new JLabel("Fecha:"));
        panel.add(new JLabel(reserva.getFecha().toString()));
        panel.add(new JLabel("Desde:"));
        panel.add(new JLabel(reserva.getHoraInicio().toString()));
        panel.add(new JLabel("Hasta:"));
        panel.add(new JLabel(reserva.getHoraFin().toString()));

        JPanel contenido = new JPanel(new BorderLayout(6, 6));
        contenido.add(panel, BorderLayout.NORTH);
        JPanel recursos = new JPanel(new BorderLayout());
        recursos.add(new JLabel("Recursos:"), BorderLayout.NORTH);
        recursos.add(scroll, BorderLayout.CENTER);
        contenido.add(recursos, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(vista, contenido, reserva.getActividad(), JOptionPane.PLAIN_MESSAGE);
    }

    private void descartarAlerta(int fila) {
        Notificacion notificacion = alertas.get(fila);

        int respuesta = JOptionPane.showConfirmDialog(vista,
                notificacion.getReserva().getActividad(), "Descartar?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) return;

        alertas.remove(fila);
        vista.modeloTabla.removeRow(fila);
    }
}
