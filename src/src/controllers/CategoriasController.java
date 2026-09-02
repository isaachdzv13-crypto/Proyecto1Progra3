package controllers;

import contexto.DatosQuemados;
import model.CategoriaRecurso;
import repository.ListaCategorias;
import view.CategoriasPanel;

import javax.swing.*;
import java.util.List;

public class CategoriasController {

    private final CategoriasPanel vista;
    private final ListaCategorias categorias = DatosQuemados.getInstancia().getCategorias();

    public CategoriasController(CategoriasPanel vista) {
        this.vista = vista;

        vista.btnBuscar.addActionListener(e -> buscar());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnBorrar.addActionListener(e -> borrar());
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnImprimir.addActionListener(e ->
                JOptionPane.showMessageDialog(vista, "Generacion de reporte en PDF pendiente de implementar."));

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });

        cargarTabla(categorias.listarTodas());
    }

    private void buscar() {
        String desc = vista.txtBuscarDescripcion.getText().trim();
        cargarTabla(categorias.buscarPorDescripcion(desc));
    }

    private void guardar() {
        String id = vista.txtId.getText().trim();
        String desc = vista.txtDescripcion.getText().trim();

        if (desc.isBlank()) {
            JOptionPane.showMessageDialog(vista, "La descripcion es obligatoria.");
            return;
        }

        if (!id.isBlank()) {
            CategoriaRecurso existente = categorias.buscarPorId(id);
            if (existente != null) {
                existente.setDesc(desc);
                JOptionPane.showMessageDialog(vista, "Categoria actualizada.");
                limpiar();
                cargarTabla(categorias.listarTodas());
                return;
            }
        }

        CategoriaRecurso nueva = new CategoriaRecurso(desc);
        categorias.addCategoria(nueva);
        JOptionPane.showMessageDialog(vista, "Categoria agregada con ID " + nueva.getId());

        limpiar();
        cargarTabla(categorias.listarTodas());
    }

    private void borrar() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione una categoria de la lista.");
            return;
        }
        String id = (String) vista.modeloTabla.getValueAt(fila, 0);
        CategoriaRecurso categoria = categorias.buscarPorId(id);

        boolean enUso = !DatosQuemados.getInstancia().getRecursos()
                .filtrarPorCategoria(categoria).isEmpty();
        if (enUso) {
            JOptionPane.showMessageDialog(vista,
                    "No se puede borrar: hay recursos asociados a esta categoria.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Desea borrar la categoria " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        categorias.borrarCategoria(categoria.getDesc());
        limpiar();
        cargarTabla(categorias.listarTodas());
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
