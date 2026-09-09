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

        });
    }
}
