package view;

import Theme.Themes;
import model.CategoriaRecurso;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CalendarizacionPanel extends JPanel {

    public JLabel lblFecha= new JLabel("Fecha");
    public JLabel lblCategoria= new JLabel("Categoria");
    public JTextField txtFecha= Themes.textField();
  public JComboBox<CategoriaRecurso> cmbCategorias= new JComboBox<>();
    public JButton btnCargar= Themes.button("Cargar",Themes.SUCCESS);
    public JButton btnImprimir= Themes.button("Imprimir",Themes.PRIMARY);

    public final DefaultTableModel modeloTabla = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable tabla = new JTable(modeloTabla);


    CalendarizacionPanel(){
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtro= new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        filtro.setBorder(BorderFactory.createTitledBorder("Filtro"));

//        filtro.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
       txtFecha.setPreferredSize(new Dimension(130,25));
       cmbCategorias.setPreferredSize(new Dimension(220,28));

       filtro.add(lblFecha);
       filtro.add(txtFecha);
       filtro.add(lblCategoria);
       filtro.add(cmbCategorias);
       filtro.add(btnCargar);
       filtro.add(btnImprimir);

       JPanel calendario= new JPanel(new BorderLayout());
       calendario.setBorder(BorderFactory.createTitledBorder("Calendarizacion de recursos"));

       calendario.add(new JScrollPane(tabla),BorderLayout.CENTER);




        add(filtro,BorderLayout.NORTH);
        add(calendario,BorderLayout.CENTER);










    }


}
