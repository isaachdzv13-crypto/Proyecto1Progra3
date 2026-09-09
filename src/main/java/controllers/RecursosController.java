package controllers;

import contexto.DatosQuemados;
import model.CategoriaRecurso;
import model.Recurso;
import repository.ListaCategorias;
import repository.ListaRecursos;
import view.RecursosPanel;

import javax.swing.*;
import java.util.List;

public class RecursosController {

    private final RecursosPanel vista;
        private final ListaRecursos recursos = DatosQuemados.getInstancia().getRecursos();
    private final ListaCategorias categorias = DatosQuemados.getInstancia().getCategorias();

    public RecursosController(RecursosPanel vista) {
        this.vista = vista;

        cargarCombos();

        vista.btnBuscar.addActionListener(e -> buscar());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnBorrar.addActionListener(e -> borrar());
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnImprimir.addActionListener(e ->
                JOptionPane.showMessageDialog(vista, "Generacion de reporte en PDF pendiente de implementar."));

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });

        cargarTabla(recursos.listarTodos());
    }

    public void cargarCombos() {
        vista.cmbFiltroCategoria.removeAllItems();
        vista.cmbFiltroCategoria.addItem(null); // representa "todas las categorias"
        vista.cmbCategoria.removeAllItems();
        vista.cmbCategoria.addItem(null);

        for (CategoriaRecurso c : categorias.listarTodas()) {
            vista.cmbFiltroCategoria.addItem(c);
            vista.cmbCategoria.addItem(c);
        }
    }

    private void buscar() {
        CategoriaRecurso categoria = (CategoriaRecurso) vista.cmbFiltroCategoria.getSelectedItem();
        String desc = vista.txtBuscarDescripcion.getText().trim();
        cargarTabla(recursos.buscar(categoria, desc));
    }

    private void guardar() {
        String id = vista.txtId.getText().trim();
        CategoriaRecurso categoria = (CategoriaRecurso) vista.cmbCategoria.getSelectedItem();
        String desc = vista.txtDescripcion.getText().trim();

        if (id.isBlank()) {
            JOptionPane.showMessageDialog(vista, "El ID  es obligatorio.");
            return;
        }
        if (categoria == null) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar una categoria.");
            return;
        }
        if (desc.isBlank()) {
            JOptionPane.showMessageDialog(vista, "La descripcion es obligatoria.");
            return;
        }

        Recurso existente = recursos.buscarPorId(id);
        if (existente != null) {
            existente.setCategoria(categoria);
            existente.setDescripcion(desc);
            guardarXML();
            JOptionPane.showMessageDialog(vista, "Recurso actualizado.");
        } else {
            recursos.addRecurso(new Recurso(id, categoria, desc));
            guardarXML();
            JOptionPane.showMessageDialog(vista, "Recurso agregado.");
        }

        limpiar();
        cargarTabla(recursos.listarTodos());
//        cargarCombos();
    }

    private void borrar() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un recurso de la lista.");
            return;
        }
        String id = (String) vista.modeloTabla.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Desea borrar el recurso " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        recursos.borrarRecurso(id);
        guardarXML();
        limpiar();
        cargarTabla(recursos.listarTodos());
//        cargarCombos();
    }

    private void limpiar() {
        vista.txtId.setText("");
        vista.txtDescripcion.setText("");
        if (vista.cmbCategoria.getItemCount() > 0) vista.cmbCategoria.setSelectedIndex(0);
        vista.tabla.clearSelection();
    }

    private void cargarSeleccion() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) return;

        String id = (String) vista.modeloTabla.getValueAt(fila, 0);
        Recurso recurso = recursos.buscarPorId(id);
        if (recurso == null) return;

        vista.txtId.setText(recurso.getId());
        vista.txtDescripcion.setText(recurso.getDescripcion());
        vista.cmbCategoria.setSelectedItem(recurso.getCategoria());
    }

    private void cargarTabla(List<Recurso> lista) {
        vista.modeloTabla.setRowCount(0);
        for (Recurso r : lista) {
            vista.modeloTabla.addRow(new Object[]{r.getId(), r.getCategoria().getDesc(), r.getDescripcion()});
        }
    }
    private void guardarXML() {
        try {
            DatosQuemados.getInstancia().guardar();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage());
        }
    }
}
