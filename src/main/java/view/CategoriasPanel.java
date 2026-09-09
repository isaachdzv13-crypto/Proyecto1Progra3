package view;

import Theme.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CategoriasPanel extends JPanel {
    public final JTextField txtBuscarDescripcion = Themes.textField();
    public final JButton btnBuscar = Themes.button("Buscar", Themes.PRIMARY);
    public final JButton btnImprimir = Themes.button("Imprimir", Themes.PRIMARY);

    public final JTextField txtId = Themes.textField();
    public final JTextField txtDescripcion = Themes.textField();

    public final JButton btnGuardar = Themes.button("Guardar", Themes.SUCCESS);
    public final JButton btnBorrar = Themes.button("Borrar", Themes.DANGER);
    public final JButton btnLimpiar = Themes.button("Limpiar", Themes.PRIMARY);

    public final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[]{"Id", "Descripcion"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    public final JTable tabla = new JTable(modeloTabla);

    public CategoriasPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId.setEnabled(false);
        txtId.setToolTipText("El ID se genera automaticamente");

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.setBorder(BorderFactory.createTitledBorder("Busqueda"));
        busqueda.add(new JLabel("Descripcion:"));
        busqueda.add(txtBuscarDescripcion);
        busqueda.add(btnBuscar);
        busqueda.add(btnImprimir);
        txtBuscarDescripcion.setPreferredSize(new Dimension(200, 26));

        JPanel formulario = new JPanel(new GridLayout(1, 4, 6, 6));
        formulario.setBorder(BorderFactory.createTitledBorder("Categoria"));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Descripcion:"));
        formulario.add(txtDescripcion);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnGuardar);
        botones.add(btnBorrar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(busqueda);
        norte.add(formulario);
        norte.add(botones);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

}
