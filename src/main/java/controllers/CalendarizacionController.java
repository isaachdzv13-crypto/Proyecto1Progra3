package controllers;

import contexto.DatosQuemados;
import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import view.CalendarizacionPanel;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CalendarizacionController {
    private final CalendarizacionPanel vista;
    private final DatosQuemados datos = DatosQuemados.getInstancia();

    public CalendarizacionController(CalendarizacionPanel vista) {
        this.vista = vista;

        vista.btnCargar.addActionListener(e -> cargarCalendarizacion());
        vista.btnImprimir.addActionListener(e ->
                util.Impresora.imprimirTabla(vista, vista.tabla, "la calendarizacion"));

        vista.txtFecha.setText(LocalDate.now().toString());
        cargarCategorias();
        cargarCalendarizacion();
    }

    public void cargarCategorias() {
        vista.cmbCategorias.removeAllItems();

        for (CategoriaRecurso categoria : datos.getCategorias().listarTodas()) {
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

        List<Recurso> recursos = datos.getRecursos().filtrarPorCategoria(categoria);

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
        for (Reserva reserva : datos.getReservas().listarTodas()) {
            boolean activa = reserva.getEstado() == Reserva.Estado.ACTIVA;
            boolean mismaFecha = reserva.getFecha().equals(fecha);
            boolean usaRecurso = reserva.getRecursos().contains(recurso);
            boolean dentroHorario = !hora.isBefore(reserva.getHoraInicio())
                    && hora.isBefore(reserva.getHoraFin());

            if (activa && mismaFecha && usaRecurso && dentroHorario) {
                Funcionario funcionario = datos.getFuncionarios()
                        .buscarPorId(reserva.getIdFuncionario());
                String nombre=funcionario.getNombre();

                return reserva.getActividad() + " - " + nombre;
            }
        }

        return "";
    }
}
