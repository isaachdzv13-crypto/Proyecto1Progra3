package controllers;

import contexto.Sesion;
import model.CategoriaRecurso;
import service.Service;
import util.GeneradorPDF;
import util.ManejadorErrores;
import view.CategoriasPanel;

import javax.swing.*;
import java.util.List;

public class CategoriasController {

    private final CategoriasPanel vista;
    private final Service service = Sesion.getService();

    public CategoriasController(CategoriasPanel vista) {
        this.vista = vista;

        vista.btnBuscar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> buscar()));
        vista.btnGuardar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> guardar()));
        vista.btnBorrar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> borrar()));
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnImprimir.addActionListener(e -> GeneradorPDF.crearPDF(vista.tabla,"Documento de categorias"));

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });

        ManejadorErrores.ejecutar(vista, () -> cargarTabla(service.listarCategorias()));
    }

    private void buscar() {
        String desc = vista.txtBuscarDescripcion.getText().trim();
        cargarTabla(service.buscarCategoriasPorDescripcion(desc));
    }

    private void guardar() {
        String id = vista.txtId.getText().trim();
        String desc = vista.txtDescripcion.getText().trim();

        if (desc.isBlank()) {
            JOptionPane.showMessageDialog(vista, "La descripcion es obligatoria.");
            return;
        }

        if (!id.isBlank()) {
            CategoriaRecurso existente = service.buscarCategoria(id);
            if (existente != null) {
                existente.setDesc(desc);
                service.actualizarCategoria(existente);
                JOptionPane.showMessageDialog(vista, "Categoria actualizada.");
                limpiar();
                cargarTabla(service.listarCategorias());
                return;
            }
        }

        CategoriaRecurso nueva = service.agregarCategoria(desc);
        JOptionPane.showMessageDialog(vista, "Categoria agregada con ID " + nueva.getId());

        limpiar();
        cargarTabla(service.listarCategorias());
    }

    private void borrar() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione una categoria de la lista.");
            return;
        }
        String id = (String) vista.modeloTabla.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Desea borrar la categoria " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        service.borrarCategoria(id);
        limpiar();
        cargarTabla(service.listarCategorias());
    }

    private void limpiar() {
        vista.txtId.setText("");
        vista.txtDescripcion.setText("");
        vista.tabla.clearSelection();
    }

    private void cargarSeleccion() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) return;

        vista.txtId.setText((String) vista.modeloTabla.getValueAt(fila, 0));
        vista.txtDescripcion.setText((String) vista.modeloTabla.getValueAt(fila, 1));
    }

    private void cargarTabla(List<CategoriaRecurso> lista) {
        vista.modeloTabla.setRowCount(0);
        for (CategoriaRecurso c : lista) {
            vista.modeloTabla.addRow(new Object[]{c.getId(), c.getDesc()});
        }
    }

}
