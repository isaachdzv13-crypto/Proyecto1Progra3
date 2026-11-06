package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AlertasPanel extends JPanel {

    public final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[]{"Actividad", "Ver", "Desc."}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    public final JTable tabla = new JTable(modeloTabla);

    public AlertasPanel() {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createTitledBorder("Alertas"));
        setPreferredSize(new Dimension(300, 0));

        tabla.setRowHeight(30);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(190);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setCellRenderer(new IconoRenderer(crearIconoVer()));
        tabla.getColumnModel().getColumn(2).setCellRenderer(new IconoRenderer(crearIconoDescartar()));
        tabla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private Icon crearIconoVer() {
        BufferedImage imagen = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(59, 130, 246));
        g.fillRoundRect(1, 4, 8, 4, 3, 3);
        g.fillRoundRect(1, 6, 18, 12, 4, 4);
        g.setColor(new Color(147, 197, 253));
        g.fillRoundRect(3, 9, 16, 9, 4, 4);
        g.dispose();
        return new ImageIcon(imagen);
    }

    private Icon crearIconoDescartar() {
        BufferedImage imagen = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(220, 38, 38));
        g.fillOval(1, 1, 18, 18);
        g.setColor(Color.WHITE);
        g.fillRect(5, 9, 10, 3);
        g.dispose();
        return new ImageIcon(imagen);
    }

    private static class IconoRenderer extends DefaultTableCellRenderer {
        private final Icon icono;

        IconoRenderer(Icon icono) {
            this.icono = icono;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            setIcon(icono);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}
