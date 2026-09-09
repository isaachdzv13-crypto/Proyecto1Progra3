package view;

import Theme.Themes;
import model.CategoriaRecurso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RecursosPanel extends JPanel {


    public final JComboBox<CategoriaRecurso> cmbFiltroCategoria = new JComboBox<>();
    public final JTextField txtBuscarDescripcion = Themes.textField();
    public final JButton btnBuscar = Themes.button("Buscar", Themes.PRIMARY);
    public final JButton btnImprimir = Themes.button("Imprimir", Themes.PRIMARY);

    public final JTextField txtId = Themes.textField();
    public final JComboBox<CategoriaRecurso> cmbCategoria = new JComboBox<>();
    public final JTextField txtDescripcion = Themes.textField();

    public final JButton btnGuardar = Themes.button("Guardar", Themes.SUCCESS);
    public final JButton btnBorrar = Themes.button("Borrar", Themes.DANGER);
    public final JButton btnLimpiar = Themes.button("Limpiar", Themes.PRIMARY);

    public final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[]{"Id", "Categoria", "Descripcion"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    public final JTable tabla = new JTable(modeloTabla);

    public RecursosPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.setBorder(BorderFactory.createTitledBorder("Filtro"));
        busqueda.add(new JLabel("Categoria:"));
        busqueda.add(cmbFiltroCategoria);
        busqueda.add(new JLabel("Descripcion:"));
        busqueda.add(txtBuscarDescripcion);
        busqueda.add(btnBuscar);
        busqueda.add(btnImprimir);
        cmbFiltroCategoria.setPreferredSize(new Dimension(180, 26));
        txtBuscarDescripcion.setPreferredSize(new Dimension(160, 26));

        JPanel formulario = new JPanel(new GridLayout(1, 6, 6, 6));
        formulario.setBorder(BorderFactory.createTitledBorder("Recurso"));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Categoria:"));
        formulario.add(cmbCategoria);
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
