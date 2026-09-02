package view;

import controllers.FuncionariosController;
import model.Administrador;

import javax.swing.*;

public class FrameAdminPrincipal extends JFrame {
    private final Administrador admin;

    public FrameAdminPrincipal(Administrador admin) {
        this.admin = admin;

        setTitle("Sistema de Reservas - " + admin.getId() + " (ADMIN)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        JTabbedPane tabs = new JTabbedPane();

        FuncionariosPanel funcionariosPanel = new FuncionariosPanel();
        new FuncionariosController(funcionariosPanel);
        tabs.addTab("Funcionarios", funcionariosPanel);


add(funcionariosPanel);

    }
}
