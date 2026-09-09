import Theme.Themes;
import controllers.LoginController;
import view.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Themes.install();

        SwingUtilities.invokeLater(() -> {
            LoginFrame ventana = new LoginFrame();
            new LoginController(ventana,null);
            ventana.setVisible(true);

//PRUEBA DE COMMITS
//            JFrame frame= new JFrame();
//
//            frame.setTitle("Prueba");
//            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
//            frame.setSize(800, 650);
//            frame.setLocationRelativeTo(null);
//            frame.setResizable(false);
//
//            CategoriasPanel panel= new CategoriasPanel();
//            frame.add(panel);
//            frame.setVisible(true);
//
//
//            FuncionariosPanel panel= new FuncionariosPanel();
//
//            frame.add(panel);
//            frame.setVisible(true);
        });
    }
}
