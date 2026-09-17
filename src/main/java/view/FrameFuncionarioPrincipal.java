package view;

import Theme.Themes;
import controllers.ActividadesController;
import controllers.CalendarizacionController;
import controllers.EstadisticasController;
import controllers.LoginController;
import controllers.ReservasController;
import model.Funcionario;

import javax.swing.*;
import java.awt.*;

public class FrameFuncionarioPrincipal extends JFrame {

    private final Funcionario funcionario;

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

        EstadisticasPanel estadisticasPanel= new EstadisticasPanel();
        new EstadisticasController(estadisticasPanel);
        tabs.add("Estadisticas",estadisticasPanel);

        JButton cerrarSesion = Themes.button("Cerrar Sesion",Themes.DANGER);
        cerrarSesion.addActionListener(e -> {
            LoginFrame login = new LoginFrame();
            new LoginController(login, null);
            login.setVisible(true);
            dispose();
        });

        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        barraSuperior.add(cerrarSesion);

        add(barraSuperior, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);


    }

}
