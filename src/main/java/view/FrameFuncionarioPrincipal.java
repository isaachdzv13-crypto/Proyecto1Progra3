package view;

import Theme.Themes;
import controllers.ActividadesController;
import controllers.CalendarizacionController;
import controllers.ReservasController;
import model.Funcionario;

import javax.swing.*;
import java.awt.*;

public class FrameFuncionarioPrincipal extends JFrame {

    private final Funcionario funcionario;
    public final JButton btnCerrarSesion = Themes.button("Cerrar sesión", Themes.DANGER);

    public FrameFuncionarioPrincipal(Funcionario funcionario){
        this.funcionario = funcionario;
        ImageIcon image= new ImageIcon("logo.png");
        Image imagen= image.getImage();
        Image imagenRedimensionada= imagen.getScaledInstance(32,32,Image.SCALE_SMOOTH);
        this.setIconImage(imagenRedimensionada);

        setTitle("Sistema de Reservas - " + funcionario.getId() + " (FUNCIONARIO)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel lblUsuario = new JLabel("Sesión: " + funcionario.getNombre() + " (Funcionario)");
        lblUsuario.setForeground(Theme.Themes.TEXT);
        barraSuperior.add(lblUsuario, BorderLayout.WEST);
        barraSuperior.add(btnCerrarSesion, BorderLayout.EAST);
        add(barraSuperior, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        ReservasPanel reservas= new ReservasPanel();
        new ReservasController(reservas,funcionario);
        tabs.add("Reservas",reservas);

        CalendarizacionPanel calendar= new CalendarizacionPanel();
        new CalendarizacionController(calendar);
        tabs.add("Calendarizacion",calendar);

        ActividadesPanel actividades= new ActividadesPanel();
        new ActividadesController(actividades);
        tabs.add("Actividades",actividades);

        AsistenteIAPanel asistenteIA = new AsistenteIAPanel();
        new controllers.AsistenteIAController(asistenteIA, reservas, tabs, 0);
        tabs.add("Asistente IA", asistenteIA);

        add(tabs, BorderLayout.CENTER);

        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        LoginFrame ventana = new LoginFrame();
        new controllers.LoginController(ventana, null);
        ventana.setVisible(true);
        dispose();
    }

}
