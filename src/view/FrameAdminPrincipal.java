package view;

import controllers.*;
import model.Administrador;

import javax.swing.*;
import java.awt.*;

public class FrameAdminPrincipal extends JFrame {
    private final Administrador admin;

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




        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == recursos) {
                control.cargarCombos();
            }
        });

add(tabs);

    }
}
