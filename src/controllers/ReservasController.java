package controllers;

import contexto.DatosQuemados;
import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import repository.ListaCategorias;
import repository.ListaRecursos;
import repository.ListaReservas;
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
    private final DatosQuemados datos = DatosQuemados.getInstancia();
    private final ListaReservas reservas = datos.getReservas();
    private final ListaRecursos recursos = datos.getRecursos();
    private final ListaCategorias categorias = datos.getCategorias();

    public ReservasController(ReservasPanel vista, Funcionario funcionario) {
        this.vista = vista;
        this.funcionario = funcionario;

        vista.btnReservar.addActionListener(e -> reservar());
        vista.btnCancelarReserva.addActionListener(e -> cancelarReserva());
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnImprimir.addActionListener(e -> JOptionPane.showMessageDialog(
                vista, "Generacion de reporte en PDF pendiente de implementar."));

        cargarCategorias();
        limpiar();
        cargarTabla();
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

        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (CategoriaRecurso categoria : seleccionadas) {
            Recurso disponible = buscarPrimerDisponible(categoria, fecha, horaInicio, horaFin);
            if (disponible == null) {
                noDisponibles.add(categoria.getDesc());
            } else {
                asignados.add(disponible);
            }
        }

        if (!noDisponibles.isEmpty()) {
            mostrarError("No hay recursos disponibles para: " + String.join(", ", noDisponibles));
            return;
        }

        Reserva reserva = new Reserva(
                funcionario.getId(), generarId(), actividad, actividad,
                horaInicio, horaFin, fecha
        );
        reserva.setRecursos(asignados);
        reservas.add(reserva);

        try {
            datos.guardar();
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(vista, "Reserva registrada correctamente.");
        limpiar();
        cargarTabla();
    }

    private Recurso buscarPrimerDisponible(CategoriaRecurso categoria, LocalDate fecha,
                                           LocalTime inicio, LocalTime fin) {
        for (Recurso recurso : recursos.filtrarPorCategoria(categoria)) {
            if (estaDisponible(recurso, fecha, inicio, fin)) return recurso;
        }
        return null;
    }

    private boolean estaDisponible(Recurso recurso, LocalDate fecha,
                                   LocalTime inicio, LocalTime fin) {
        for (Reserva reserva : reservas.listarTodas()) {
            if (reserva.getEstado() == Reserva.Estado.CANCELADA) continue;
            if (!reserva.getFecha().equals(fecha)) continue;
            if (!reserva.getRecursos().contains(recurso)) continue;

            boolean horariosSeCruzan = inicio.isBefore(reserva.getHoraFin())
                    && fin.isAfter(reserva.getHoraInicio());
            if (horariosSeCruzan) return false;
        }
        return true;
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

        String error = reservas.cancelar(id);
        if (error != null) {
            mostrarError(error);
            return;
        }

        try {
            datos.guardar();
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(vista, "Reserva cancelada.");
        cargarTabla();
    }

    private String generarId() {
        int mayor = 0;
        for (Reserva reserva : reservas.listarTodas()) {
            String id = reserva.getIdReserva();
            if (id != null && id.startsWith("RES-")) {
                try {
                    mayor = Math.max(mayor, Integer.parseInt(id.substring(4)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("RES-%06d", mayor + 1);
    }

    private void cargarCategorias() {
        vista.modeloCategorias.clear();
        for (CategoriaRecurso categoria : categorias.listarTodas()) {
            vista.modeloCategorias.addElement(categoria);
        }
    }

    private void cargarTabla() {
        vista.modeloTabla.setRowCount(0);
        for (Reserva reserva : reservas.listarPorFuncionario(funcionario.getId())) {
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

