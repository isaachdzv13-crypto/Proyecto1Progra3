package controllers;

import contexto.DatosQuemados;
import model.Funcionario;
import model.Reserva;
import view.ActividadesPanel;

import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActividadesController {

    private final ActividadesPanel vista;
    private final DatosQuemados datos = DatosQuemados.getInstancia();

    public ActividadesController(ActividadesPanel vista) {
        this.vista = vista;

        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnImprimir.addActionListener(e ->
                util.Impresora.imprimirTabla(vista, vista.tabla, "el listado de actividades"));

        vista.txtFecha.setText(LocalDate.now().toString());
        cargar();
    }

    public void cargar() {
        LocalDate fecha;

        try {
            fecha = LocalDate.parse(vista.txtFecha.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Fecha incorrecta. Use AAAA-MM-DD.");
            return;
        }

        LocalDate lunes = fecha.with(DayOfWeek.MONDAY);
        String[] dias = {"Lunes", "Martes", "Miercoles", "Jueves",
                "Viernes", "Sabado", "Domingo"};

        Object[] columnas = new Object[8];
        columnas[0] = "Hora";

        for (int i = 0; i < 7; i++) {
            columnas[i + 1] = dias[i] + " " + lunes.plusDays(i);
        }

        vista.modeloTabla.setColumnIdentifiers(columnas);
        vista.modeloTabla.setRowCount(0);

        for (int hora = 6; hora <= 22; hora++) {
            Object[] fila = new Object[8];
            fila[0] = String.format("%02d:00", hora);

            for (int dia = 0; dia < 7; dia++) {
                fila[dia + 1] = buscarActividades(
                        lunes.plusDays(dia), LocalTime.of(hora, 0));
            }

            vista.modeloTabla.addRow(fila);
        }

        for (int i = 0; i < 8; i++) {
            vista.tabla.getColumnModel().getColumn(i).setPreferredWidth(190);
        }
        vista.tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        vista.tabla.setRowHeight(28);
    }

    private String buscarActividades(LocalDate fecha, LocalTime hora) {
        String texto = "";

        for (Reserva reserva : datos.getReservas().listarTodas()) {
            if (reserva.getEstado() == Reserva.Estado.ACTIVA
                    && reserva.getFecha().equals(fecha)
                    && !hora.isBefore(reserva.getHoraInicio())
                    && hora.isBefore(reserva.getHoraFin())) {

                if (!texto.isEmpty()) {
                    texto = texto + " | ";
                }

                texto = texto + reserva.getActividad() + "-"
                        + buscarNombre(reserva.getIdFuncionario());
            }
        }

        if (texto.isEmpty()) {
            texto = "";
        }

        return texto;
    }

    private String buscarNombre(String id) {
        Funcionario funcionario = datos.getFuncionarios().buscarPorId(id);

        if (funcionario == null) {
            return id;
        }

        return funcionario.getNombre();
    }
}
