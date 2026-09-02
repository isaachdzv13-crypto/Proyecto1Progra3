package view;

import Theme.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FuncionariosPanel extends JPanel {

    public final JTextField txtBuscarId = Themes.textField();
    public final JTextField txtBuscarNombre = Themes.textField();
    public final JButton btnBuscar = Themes.button("Buscar", Themes.PRIMARY);
    public final JButton btnImprimir = Themes.button("Imprimir", Themes.PRIMARY);

    public final JTextField txtId = Themes.textField();
    public final JTextField txtNombre = Themes.textField();
    public final JTextField txtTelefono = Themes.textField();

    public final JButton btnGuardar = Themes.button("Guardar", Themes.SUCCESS);
    public final JButton btnBorrar = Themes.button("Borrar", Themes.DANGER);
    public final JButton btnLimpiar = Themes.button("Limpiar", Themes.PRIMARY);

    public final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[]{"Id", "Nombre", "Telefono"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    public final JTable tabla = new JTable(modeloTabla);


public FuncionariosPanel(){
    setLayout(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
    busqueda.setBorder(BorderFactory.createTitledBorder("Busqueda"));
    busqueda.add(new JLabel("ID:"));
    busqueda.add(txtBuscarId);
    busqueda.add(new JLabel("Nombre:"));
    busqueda.add(txtBuscarNombre);
    busqueda.add(btnBuscar);
    busqueda.add(btnImprimir);
    txtBuscarId.setPreferredSize(new Dimension(100, 26));
    txtBuscarNombre.setPreferredSize(new Dimension(160, 26));


    JPanel formulario = new JPanel(new GridLayout(1, 6, 6, 6));
    formulario.setBorder(BorderFactory.createTitledBorder("Funcionario"));
    formulario.add(new JLabel("ID:"));
    formulario.add(txtId);
    formulario.add(new JLabel("Nombre:"));
    formulario.add(txtNombre);
    formulario.add(new JLabel("Telefono:"));
    formulario.add(txtTelefono);

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
