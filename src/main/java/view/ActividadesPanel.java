package view;

import Theme.Themes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ActividadesPanel extends JPanel {
    public JTextField txtFecha= Themes.textField();
   public  JButton btnCargar= Themes.button("Cargas",Themes.SUCCESS);
   public  JButton btnImprimir= Themes.button("Imprimir",Themes.PRIMARY);
    public final DefaultTableModel modeloTabla = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable tabla = new JTable(modeloTabla);

    public ActividadesPanel(){
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel busqueda=new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.setBorder(BorderFactory.createTitledBorder("Semana"));
        busqueda.add(new JLabel("Fecha de referencia"));
        busqueda.add(txtFecha);
        busqueda.add(btnCargar);
        busqueda.add(btnImprimir);
        txtFecha.setPreferredSize(new Dimension(160, 26));

        JPanel calendario= new JPanel(new BorderLayout());
        calendario.setBorder(BorderFactory.createTitledBorder("Actividades Semanales"));

        calendario.add(new JScrollPane(tabla),BorderLayout.CENTER);


        add(busqueda,BorderLayout.NORTH);
        add(calendario,BorderLayout.CENTER);




    }
}
