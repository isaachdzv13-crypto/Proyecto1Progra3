package view;

import Theme.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EstadisticasPanel extends JPanel {

    public final JLabel lblFuncionarios = crearLabelValor();
    public final JLabel lblCategorias = crearLabelValor();
    public final JLabel lblRecursos = crearLabelValor();
    public final JLabel lblReservasActivas = crearLabelValor();
    public final JLabel lblReservasCanceladas = crearLabelValor();

    public final JButton btnActualizar = Themes.button("Actualizar", Themes.PRIMARY);

    public final DefaultTableModel modeloRecursosPorCategoria =
            new DefaultTableModel(new Object[]{"Categoria", "Cantidad de recursos"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    public final JTable tablaRecursosPorCategoria = new JTable(modeloRecursosPorCategoria);

    public EstadisticasPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel resumen = new JPanel(new GridLayout(0, 2, 12, 8));
        resumen.setBorder(BorderFactory.createTitledBorder("Resumen general"));

        resumen.add(new JLabel("Funcionarios registrados:"));
        resumen.add(lblFuncionarios);

        resumen.add(new JLabel("Categorias de recursos:"));
        resumen.add(lblCategorias);

        resumen.add(new JLabel("Recursos totales:"));
        resumen.add(lblRecursos);

        resumen.add(new JLabel("Reservas activas:"));
        resumen.add(lblReservasActivas);

        resumen.add(new JLabel("Reservas canceladas:"));
        resumen.add(lblReservasCanceladas);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(resumen, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnActualizar);
        norte.add(botones, BorderLayout.SOUTH);

        JPanel tablaPanel = new JPanel(new BorderLayout());
        tablaPanel.setBorder(BorderFactory.createTitledBorder("Recursos por categoria"));
        tablaPanel.add(new JScrollPane(tablaRecursosPorCategoria), BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);
        add(tablaPanel, BorderLayout.CENTER);
    }

    private static JLabel crearLabelValor() {
        JLabel label = new JLabel("0");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }
}
