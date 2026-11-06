package controllers;

import contexto.Sesion;
import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import service.Service;
import util.GeneradorPDF;
import util.ManejadorErrores;
import view.ReservasPanel;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservasController {

    private final ReservasPanel vista;
    private final Funcionario funcionario;
    private final Service service = Sesion.getService();

    public ReservasController(ReservasPanel vista, Funcionario funcionario) {
        this.vista = vista;
        this.funcionario = funcionario;

        vista.btnReservar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> reservar()));
        vista.btnCancelarReserva.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> cancelarReserva()));
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnImprimir.addActionListener(e -> GeneradorPDF.crearPDF(vista.tabla,"Reservas"));

        limpiar();
        ManejadorErrores.ejecutar(vista, () -> {
            cargarCategorias();
            cargarTabla();
        });
    }

    private void reservar() {
        String actividad = vista.txtActividad.getText().trim();
        if (actividad.isBlank()) {
            mostrarError("La actividad es obligatoria.");
            return;
        }

        LocalDate fecha;
        LocalTime horaInicio;
        LocalTime horaFin;
        try {
            fecha = LocalDate.parse(vista.txtFecha.getText().trim());
            horaInicio = LocalTime.parse(vista.cmbHoraInicio.getSelectedItem().toString());
            horaFin = LocalTime.parse(vista.cmbHoraFin.getSelectedItem().toString());
        } catch (DateTimeParseException ex) {
            mostrarError("Use fecha AAAA-MM-DD y horas HH:mm.");
            return;
        }

        if (!horaFin.isAfter(horaInicio)) {
            mostrarError("La hora final debe ser posterior a la hora de inicio.");
            return;
        }

        List<CategoriaRecurso> seleccionadas = vista.listaCategorias.getSelectedValuesList();
        if (seleccionadas.isEmpty()) {
            mostrarError("Seleccione al menos una categoria.");
            return;
        }

        List<String> idsCategorias = new ArrayList<>();
        for (CategoriaRecurso categoria : seleccionadas) {
            idsCategorias.add(categoria.getId());
        }

        service.crearReserva(funcionario.getId(), actividad, fecha, horaInicio, horaFin, idsCategorias);

        JOptionPane.showMessageDialog(vista, "Reserva registrada correctamente.");
        limpiar();
        cargarTabla();
    }

    private void cancelarReserva() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccione una reserva de la lista.");
            return;
        }

        String id = vista.modeloTabla.getValueAt(fila, 0).toString();
        int respuesta = JOptionPane.showConfirmDialog(
                vista, "¿Desea cancelar la reserva " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) return;

        service.cancelarReserva(id, funcionario.getId());

        JOptionPane.showMessageDialog(vista, "Reserva cancelada.");
        cargarTabla();
    }

    private void cargarCategorias() {
        vista.modeloCategorias.clear();
        for (CategoriaRecurso categoria : service.listarCategorias()) {
            vista.modeloCategorias.addElement(categoria);
        }
    }

    private void cargarTabla() {
        vista.modeloTabla.setRowCount(0);
        for (Reserva reserva : service.listarReservasPorFuncionario(funcionario.getId())) {
            String idsRecursos = reserva.getRecursos().stream()
                    .map(Recurso::getId)
                    .collect(Collectors.joining(", "));
            String horario = reserva.getHoraInicio() + " - " + reserva.getHoraFin();
            vista.modeloTabla.addRow(new Object[]{
                    reserva.getIdReserva(), reserva.getActividad(), reserva.getFecha(),
                    horario, idsRecursos, reserva.getEstado()
            });
        }
    }

    private void limpiar() {
        vista.txtDescripcion.setText("");
        vista.txtActividad.setText("");
        vista.txtFecha.setText(LocalDate.now().plusDays(1).toString());
        vista.cmbHoraInicio.setSelectedItem("08:00");
        vista.cmbHoraFin.setSelectedItem("09:00");
        vista.listaCategorias.clearSelection();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
