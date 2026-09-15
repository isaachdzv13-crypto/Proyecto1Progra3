package controllers;

import contexto.DatosQuemados;
import model.Recurso;
import model.Reserva;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import view.EstadisticasPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class EstadisticasController {

    private final EstadisticasPanel vista;
    private final DatosQuemados datos =
            DatosQuemados.getInstancia();

    public EstadisticasController(EstadisticasPanel vista) {
        this.vista = vista;

        vista.getBtnCargar().addActionListener(
                e -> cargar()
        );
    }

    private void cargar() {
        LocalDate desde;
        LocalDate hasta;

        try {
            desde = LocalDate.parse(
                    vista.getTxtDesde().getText()
            );

            hasta = LocalDate.parse(
                    vista.getTxtHasta().getText()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Use fechas con formato AAAA-MM-DD"
            );
            return;
        }

        if (hasta.isBefore(desde)) {
            JOptionPane.showMessageDialog(
                    vista,
                    "La fecha hasta debe ser posterior"
            );
            return;
        }

        Map<String, Integer> cantidades =
                new LinkedHashMap<>();

        for (Reserva reserva :
                datos.getReservas().listarTodas()) {

            if (reserva.getEstado()
                    == Reserva.Estado.CANCELADA) {
                continue;
            }

            if (reserva.getFecha().isBefore(desde)
                    || reserva.getFecha().isAfter(hasta)) {
                continue;
            }

            for (Recurso recurso :
                    reserva.getRecursos()) {

                String categoria =
                        recurso.getCategoria().getDesc();

                int cantidad = 0;

                if (cantidades.containsKey(categoria)) {
                    cantidad = cantidades.get(categoria);
                }

                cantidades.put(
                        categoria,
                        cantidad + 1
                );
            }
        }

        vista.modeloTabla.setRowCount(0);

        DefaultCategoryDataset datosGrafico =
                new DefaultCategoryDataset();

        for (String categoria : cantidades.keySet()) {
            int cantidad = cantidades.get(categoria);

            vista.modeloTabla.addRow(
                    new Object[]{categoria, cantidad}
            );

            datosGrafico.addValue(
                    cantidad,
                    "Recursos",
                    categoria
            );
        }

        JFreeChart grafico =
                ChartFactory.createBarChart(
                        "Recursos usados",
                        "Categoria",
                        "Cantidad",
                        datosGrafico
                );

        ChartPanel chartPanel =
                new ChartPanel(grafico);

        vista.getPanelGrafico().removeAll();

        vista.getPanelGrafico().add(
                chartPanel,
                BorderLayout.CENTER
        );

        vista.getPanelGrafico().revalidate();
        vista.getPanelGrafico().repaint();
    }
}
