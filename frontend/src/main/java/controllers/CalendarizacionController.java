package controllers;

import contexto.Sesion;
import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import service.Service;
import util.GeneradorPDF;
import util.ManejadorErrores;
import view.CalendarizacionPanel;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarizacionController {
    private final CalendarizacionPanel vista;
    private final Service service = Sesion.getService();
    private List<Reserva> reservas = new ArrayList<>();
    private Map<String, String> nombres = new HashMap<>();

    public CalendarizacionController(CalendarizacionPanel vista) {
        this.vista = vista;

        vista.btnCargar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> cargarCalendarizacion()));
        vista.btnImprimir.addActionListener(e -> GeneradorPDF.crearPDF(vista.tabla,"Calendarizacion de actividades"));


        vista.txtFecha.setText(LocalDate.now().toString());
        ManejadorErrores.ejecutar(vista, () -> {
            cargarCategorias();
            cargarCalendarizacion();
        });
    }

    public void cargarCategorias() {
        vista.cmbCategorias.removeAllItems();

        for (CategoriaRecurso categoria : service.listarCategorias()) {
            vista.cmbCategorias.addItem(categoria);
        }
    }

    public void cargarCalendarizacion() {
        CategoriaRecurso categoria = (CategoriaRecurso) vista.cmbCategorias.getSelectedItem();

        if (categoria == null) {
            vista.modeloTabla.setColumnIdentifiers(new Object[]{"Hora"});
            vista.modeloTabla.setRowCount(0);
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(vista.txtFecha.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(vista,
                    "Use el formato de fecha AAAA-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Recurso> recursos = service.buscarRecursos(categoria.getId(), "");
        reservas = service.listarReservas();
        nombres = new HashMap<>();
        for (Funcionario f : service.listarFuncionarios()) {
            nombres.put(f.getId(), f.getNombre());
        }

        Object[] columnas = new Object[recursos.size() + 1];
        columnas[0] = "Hora";

        for (int i = 0; i < recursos.size(); i++) {
            columnas[i + 1] = recursos.get(i).getDescripcion();
        }

        vista.modeloTabla.setColumnIdentifiers(columnas);
        vista.modeloTabla.setRowCount(0);

        for (int hora = 6; hora <= 22; hora++) {
            Object[] fila = new Object[recursos.size() + 1];
            fila[0] = String.format("%02d:00", hora);

            for (int i = 0; i < recursos.size(); i++) {
                fila[i + 1] = obtenerContenido(
                        recursos.get(i), fecha, LocalTime.of(hora, 0));
            }

            vista.modeloTabla.addRow(fila);
        }

        vista.tabla.getColumnModel().getColumn(0).setPreferredWidth(65);
        for (int i = 1; i < vista.tabla.getColumnCount(); i++) {
            vista.tabla.getColumnModel().getColumn(i).setPreferredWidth(230);
        }
        vista.tabla.setRowHeight(28);
    }

    private String obtenerContenido(Recurso recurso, LocalDate fecha, LocalTime hora) {
        for (Reserva reserva : reservas) {
            boolean activa = reserva.getEstado() == Reserva.Estado.ACTIVA;
            boolean mismaFecha = reserva.getFecha().equals(fecha);
            boolean usaRecurso = reserva.getRecursos().contains(recurso);
            boolean dentroHorario = !hora.isBefore(reserva.getHoraInicio())
                    && hora.isBefore(reserva.getHoraFin());

            if (activa && mismaFecha && usaRecurso && dentroHorario) {
                String nombre = nombres.getOrDefault(reserva.getIdFuncionario(), reserva.getIdFuncionario());

                return reserva.getActividad() + " - " + nombre;
            }
        }

        return "";
    }
}
