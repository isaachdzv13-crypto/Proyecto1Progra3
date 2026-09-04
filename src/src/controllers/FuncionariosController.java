package controllers;

import contexto.DatosQuemados;
import model.Funcionario;
import repository.ListaFuncionarios;
import view.FuncionariosPanel;

import javax.swing.*;
import java.util.List;

public class FuncionariosController {
    private final FuncionariosPanel vista;
    private final ListaFuncionarios funcionarios = DatosQuemados.getInstancia().getFuncionarios();

    public FuncionariosController(FuncionariosPanel vista) {
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

        cargarTabla(funcionarios.listarTodos());
    }
    private void buscar() {
        String id = vista.txtBuscarId.getText().trim();
        String nombre = vista.txtBuscarNombre.getText().trim();

        List<Funcionario> resultado;
        if (!id.isBlank()) {
            Funcionario f = funcionarios.buscarPorId(id);
            resultado = f != null ? List.of(f) : List.of();
        } else {
            resultado = funcionarios.buscarPorNombre(nombre);
        }
        cargarTabla(resultado);
    }

    private void guardar() {
        String id = vista.txtId.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String telefono = vista.txtTelefono.getText().trim();

        if (id.isBlank()) {
            JOptionPane.showMessageDialog(vista, "El ID es obligatorio.");
            return;
        }
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(vista, "El nombre es obligatorio.");
            return;
        }
        if (telefono.isBlank()) {
            JOptionPane.showMessageDialog(vista, "El telefono es obligatorio.");
            return;
        }

        Funcionario existente = funcionarios.buscarPorId(id);
        if (existente != null) {
            existente.setNombre(nombre);
            existente.setTelefono(telefono);
            guardarXML();
            JOptionPane.showMessageDialog(vista, "Funcionario actualizado.");
        } else {
            try {
                Funcionario nuevo = new Funcionario(id, nombre, telefono);
                funcionarios.addRecurso(nuevo);
                guardarXML();
                JOptionPane.showMessageDialog(vista,
                        "Funcionario agregado. Su clave inicial es igual al ID.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(vista, ex.getMessage());
                return;
            }
        }

        limpiar();
        cargarTabla(funcionarios.listarTodos());
    }

    private void borrar() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un funcionario de la lista.");
            return;
        }
        String id = (String) vista.modeloTabla.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Desea borrar al funcionario " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        funcionarios.borrarRecurso(id);
        guardarXML();
        limpiar();
        cargarTabla(funcionarios.listarTodos());
    }

    private void limpiar() {


        vista.txtId.setText("");
        vista.txtNombre.setText("");
        vista.txtTelefono.setText("");
        vista.tabla.clearSelection();
    }

    private void cargarSeleccion() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) return;

        vista.txtId.setText((String) vista.modeloTabla.getValueAt(fila, 0));
        vista.txtNombre.setText((String) vista.modeloTabla.getValueAt(fila, 1));
        vista.txtTelefono.setText((String) vista.modeloTabla.getValueAt(fila, 2));
    }

    private void cargarTabla(List<Funcionario> lista) {
        vista.modeloTabla.setRowCount(0);
        for (Funcionario f : lista) {
            vista.modeloTabla.addRow(new Object[]{f.getId(), f.getNombre(), f.getTelefono()});
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
