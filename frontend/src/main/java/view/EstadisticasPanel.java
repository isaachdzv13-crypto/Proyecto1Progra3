package view;

import Theme.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EstadisticasPanel extends JPanel {

    private JTextField txtDesde = new JTextField(10);
    private JTextField txtHasta = new JTextField(10);
    private JButton btnCargar = Themes.button("Cargar",Themes.SUCCESS);

    public JTextField getTxtDesde2() {
        return txtDesde2;
    }

    public JTextField getTxtHasta2() {
        return txtHasta2;
    }

    private final JTextField txtDesde2 = new JTextField(10);
    private final JTextField txtHasta2 = new JTextField(10);

    public JButton getBtnCargar2() {
        return btnCargar2;
    }

    private final JButton btnCargar2 = Themes.button("Cargar",Themes.SUCCESS);

    private final JPanel panelGrafico =
            new JPanel(new BorderLayout());

    public JPanel getPanelGrafico2() {
        return panelGrafico2;
    }

    private final JPanel panelGrafico2 =
            new JPanel(new BorderLayout());

    public final DefaultTableModel modeloTabla =
            new DefaultTableModel(
                    new Object[]{"Categoria", "Cantidad"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    public final JTable tabla = new JTable(modeloTabla);

    public final DefaultTableModel modeloTabla2 =
            new DefaultTableModel(
                    new Object[]{"Semana", "Cantidad"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    public final JTable tabla2 = new JTable(modeloTabla2);

    public EstadisticasPanel() {
        setLayout(new GridLayout(1, 2));

        add(configurarPanelIzquierda());
        add(configurarPanelDerecha());
    }

    JPanel configurarPanelIzquierda() {
        JPanel main = new JPanel(new BorderLayout(5, 5));

        main.setBorder(BorderFactory.createTitledBorder("Recursos"));

        JPanel fechas = new JPanel(new FlowLayout(FlowLayout.LEFT));

        fechas.setBorder(BorderFactory.createTitledBorder("Fechas Desde y Hasta"));

        fechas.add(new JLabel("Desde:"));
        fechas.add(txtDesde);

        fechas.add(new JLabel("Hasta:"));
        fechas.add(txtHasta);

        fechas.add(btnCargar);

        JPanel estadisticas = new JPanel(new BorderLayout());

        estadisticas.setBorder(BorderFactory.createTitledBorder("Estadisticas"));

        estadisticas.add(new JScrollPane(tabla), BorderLayout.CENTER);

        panelGrafico.setBorder(BorderFactory.createTitledBorder("Grafico"));

        panelGrafico.setPreferredSize(new Dimension(0, 300));

        main.add(fechas, BorderLayout.NORTH);
        main.add(estadisticas, BorderLayout.CENTER);
        main.add(panelGrafico, BorderLayout.SOUTH);

        return main;
    }

    JPanel configurarPanelDerecha() {
        JPanel principal = new JPanel(new BorderLayout(5, 5));
        principal.setBorder(BorderFactory.createTitledBorder("Actividades"));

        JPanel fechas= new JPanel(new FlowLayout(FlowLayout.LEFT));
        fechas.setBorder(BorderFactory.createTitledBorder("Fechas Desde y Hasta"));
        fechas.add(new JLabel("Desde: "));
        fechas.add(txtDesde2);
        fechas.add(new JLabel("Hasta: "));
        fechas.add(txtHasta2);
        fechas.add(btnCargar2);

        JPanel estadisticas= new JPanel(new BorderLayout());
        estadisticas.setBorder(BorderFactory.createTitledBorder("Estadisticas"));
        estadisticas.add(new JScrollPane(tabla2),BorderLayout.CENTER);

        panelGrafico2.setBorder(BorderFactory.createTitledBorder("Grafico"));

        panelGrafico2.setPreferredSize(new Dimension(0, 300));



        principal.add(fechas,BorderLayout.NORTH);
        principal.add(estadisticas,BorderLayout.CENTER);
        principal.add(panelGrafico2, BorderLayout.SOUTH);
        return principal;
    }

    public JTextField getTxtDesde() {
        return txtDesde;
    }

    public void setTxtDesde(JTextField txtDesde) {
        this.txtDesde = txtDesde;
    }

    public JTextField getTxtHasta() {
        return txtHasta;
    }

    public void setTxtHasta(JTextField txtHasta) {
        this.txtHasta = txtHasta;
    }

    public JButton getBtnCargar() {
        return btnCargar;
    }

    public void setBtnCargar(JButton btnCargar) {
        this.btnCargar = btnCargar;
    }

    public JPanel getPanelGrafico() {
        return panelGrafico;
    }
}
