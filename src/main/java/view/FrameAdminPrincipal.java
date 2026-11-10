package view;

import Theme.Themes;
import controllers.*;
import model.Administrador;

import javax.swing.*;
import java.awt.*;

public class FrameAdminPrincipal extends JFrame {
    private final Administrador admin;
    public final JButton btnCerrarSesion = Themes.button("Cerrar sesión", Themes.DANGER);

    public FrameAdminPrincipal(Administrador admin) {
        this.admin = admin;
        ImageIcon image= new ImageIcon("logo.png");
        Image imagen= image.getImage();
        Image imagenRedimensionada= imagen.getScaledInstance(32,32,Image.SCALE_SMOOTH);
        this.setIconImage(imagenRedimensionada);


        setTitle("Sistema de Reservas - " + admin.getId() + " (ADMIN)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel lblUsuario = new JLabel("Sesión: " + admin.getId() + " (Administrador)");
        lblUsuario.setForeground(Theme.Themes.TEXT);
        barraSuperior.add(lblUsuario, BorderLayout.WEST);
        barraSuperior.add(btnCerrarSesion, BorderLayout.EAST);
        add(barraSuperior, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        FuncionariosPanel funcionariosPanel = new FuncionariosPanel();
        new FuncionariosController(funcionariosPanel);
        tabs.addTab("Funcionarios", funcionariosPanel);

        CategoriasPanel categorias= new CategoriasPanel();
        new CategoriasController(categorias);
        tabs.addTab("Categorias",categorias);

        RecursosPanel recursos= new RecursosPanel();
        RecursosController control=new RecursosController(recursos);
        tabs.add("Recursos",recursos);

        CalendarizacionPanel calendario= new CalendarizacionPanel();
        CalendarizacionController controle= new CalendarizacionController(calendario);
        tabs.add("Calendarizacion",calendario);

        ActividadesPanel actividades= new ActividadesPanel();
        new ActividadesController(actividades);
        tabs.add("Actividades",actividades);

        EstadisticasPanel estadisticas = new EstadisticasPanel();
        EstadisticasController controladorEstadisticas = new EstadisticasController(estadisticas);
        tabs.add("Estadisticas", estadisticas);


        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == recursos) {
                control.cargarCombos();
            }
            if (tabs.getSelectedComponent() == estadisticas) {
                controladorEstadisticas.actualizar();
            }
        });

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
