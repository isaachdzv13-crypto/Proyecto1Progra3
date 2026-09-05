package view;

import Theme.Themes;
import model.CategoriaRecurso;

import javax.swing.*;

public class ReservasPanel extends JPanel {
    public final JTextField txtDescripcion = Themes.textField();
    public final JTextField txtActividad = Themes.textField();
    public final JTextField txtFecha= Themes.textField();

    public final JButton btnExtraerIA= Themes.button("Extraer",Themes.PRIMARY);
    public final JButton btnReservar= Themes.button("Reservar",Themes.SUCCESS);
    public final JButton btnCancelarSeleccion= Themes.button("Cancelar Reserva",Themes.DANGER);
    public final JButton btnLimpiar= Themes.button("Limpiar",Themes.PRIMARY);

    public final JComboBox<String> cmbJHoraInicio= new JComboBox<>();
    public final JComboBox<String> cmbJHoraFinal= new JComboBox<>();

    public final DefaultListModel<CategoriaRecurso> listaCategorias= new DefaultListModel<>();



}
