package view;

import Theme.Themes;
import model.CategoriaRecurso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReservasPanel extends JPanel {

    public final JTextField txtDescripcion = Themes.textField();
    public final JTextField txtActividad = Themes.textField();
    public final JTextField txtFecha = Themes.textField();
    public final JComboBox<String> cmbHoraInicio = new JComboBox<>();
    public final JComboBox<String> cmbHoraFin = new JComboBox<>();

    public final DefaultListModel<CategoriaRecurso> modeloCategorias = new DefaultListModel<>();
    public final JList<CategoriaRecurso> listaCategorias = new JList<>(modeloCategorias);

    public final JButton btnReservar = Themes.button("Reservar", Themes.SUCCESS);
    public final JButton btnCancelarReserva = Themes.button("Cancelar reserva", Themes.DANGER);
    public final JButton btnLimpiar = Themes.button("Limpiar", Themes.PRIMARY);
    public final JButton btnImprimir = Themes.button("Imprimir", Themes.PRIMARY);

    public final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public final JTable tabla = new JTable(modeloTabla);

    public ReservasPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createTitledBorder("Nueva reserva"));

        formulario.add(crearFila("Descripcion",txtDescripcion));
        formulario.add(Box.createVerticalStrut(5));
        formulario.add(crearFila("Actividad:", txtActividad));
        formulario.add(Box.createVerticalStrut(5));
        formulario.add(crearFila("Fecha (AAAA-MM-DD):", txtFecha));
        formulario.add(Box.createVerticalStrut(5));
        cargarHoras();
        formulario.add(crearFila("Hora inicio:", cmbHoraInicio));
        formulario.add(Box.createVerticalStrut(5));
        formulario.add(crearFila("Hora fin:", cmbHoraFin));
        formulario.add(Box.createVerticalStrut(5));

        listaCategorias.setSelectionModel(new DefaultListSelectionModel() {
            {
                setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }

            @Override
            public void setSelectionInterval(int inicio, int fin) {
                if (isSelectedIndex(inicio)) {
                    removeSelectionInterval(inicio, fin);
                } else {
                    addSelectionInterval(inicio, fin);
                }
            }
        });
        listaCategorias.setToolTipText("Haga clic para seleccionar o quitar categorias");
        listaCategorias.setVisibleRowCount(4);
        JScrollPane scrollCategorias = new JScrollPane(listaCategorias);
        scrollCategorias.setPreferredSize(new Dimension(350, 85));
        formulario.add(crearFila("Categorias requeridas:", scrollCategorias));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnReservar);
        botones.add(btnCancelarReserva);
        botones.add(btnLimpiar);
        botones.add(btnImprimir);

        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(formulario);
        superior.add(botones);

        JPanel listado = new JPanel(new BorderLayout());
        listado.setBorder(BorderFactory.createTitledBorder("Mis reservas"));
        tabla.setFillsViewportHeight(true);
        listado.add(new JScrollPane(tabla), BorderLayout.CENTER);



        add(superior, BorderLayout.NORTH);
        add(listado, BorderLayout.CENTER);
    }

    private JPanel crearFila(String texto, JComponent componente) {
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setPreferredSize(new Dimension(155, 26));
        fila.add(etiqueta, BorderLayout.WEST);
        fila.add(componente, BorderLayout.CENTER);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                componente instanceof JScrollPane ? 90 : 30));
        return fila;
    }

    private void cargarHoras() {
        for (int hora = 6; hora <= 22; hora++) {
            String valor = String.format("%02d:00", hora);
            cmbHoraInicio.addItem(valor);
            cmbHoraFin.addItem(valor);
        }
    }


}
