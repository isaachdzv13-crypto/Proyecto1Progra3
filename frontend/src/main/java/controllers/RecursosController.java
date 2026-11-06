package controllers;

import contexto.Sesion;
import model.CategoriaRecurso;
import model.Recurso;
import service.Service;
import util.GeneradorPDF;
import util.ManejadorErrores;
import view.RecursosPanel;

import javax.swing.*;
import java.util.List;

public class RecursosController {

    private final RecursosPanel vista;
    private final Service service = Sesion.getService();

    public RecursosController(RecursosPanel vista) {
        this.vista = vista;

        ManejadorErrores.ejecutar(vista, () -> cargarCombos());

        vista.btnBuscar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> buscar()));
        vista.btnGuardar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> guardar()));
        vista.btnBorrar.addActionListener(e -> ManejadorErrores.ejecutar(vista, () -> borrar()));
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnImprimir.addActionListener(e -> GeneradorPDF.crearPDF(vista.tabla,"Lista de Recursos"));

        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) ManejadorErrores.ejecutar(vista, () -> cargarSeleccion());
        });

        ManejadorErrores.ejecutar(vista, () -> cargarTabla(service.listarRecursos()));
    }

    public void cargarCombos() {
        vista.cmbFiltroCategoria.removeAllItems();
        vista.cmbFiltroCategoria.addItem(null);
        vista.cmbCategoria.removeAllItems();
        vista.cmbCategoria.addItem(null);

        for (CategoriaRecurso c : service.listarCategorias()) {
            vista.cmbFiltroCategoria.addItem(c);
            vista.cmbCategoria.addItem(c);
        }
    }

    private void buscar() {
        CategoriaRecurso categoria = (CategoriaRecurso) vista.cmbFiltroCategoria.getSelectedItem();
        String desc = vista.txtBuscarDescripcion.getText().trim();
        String idCategoria = categoria == null ? null : categoria.getId();
        cargarTabla(service.buscarRecursos(idCategoria, desc));
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

        Recurso existente = service.buscarRecurso(id);
        if (existente != null) {
            existente.setCategoria(categoria);
            existente.setDescripcion(desc);
            service.actualizarRecurso(existente);
            JOptionPane.showMessageDialog(vista, "Recurso actualizado.");
        } else {
            service.agregarRecurso(new Recurso(id, categoria, desc));
            JOptionPane.showMessageDialog(vista, "Recurso agregado.");
        }

        limpiar();
        cargarTabla(service.listarRecursos());
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

        service.borrarRecurso(id);
        limpiar();
        cargarTabla(service.listarRecursos());
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
        Recurso recurso = service.buscarRecurso(id);
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
}
