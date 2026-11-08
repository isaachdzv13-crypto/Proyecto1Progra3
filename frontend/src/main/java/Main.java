import Theme.Themes;
import contexto.Sesion;
import controllers.LoginController;
import view.LoginFrame;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Themes.install();

        String host = JOptionPane.showInputDialog(
                null,
                "Dirección del servidor:",
                "localhost"
        );

        if (host == null || host.isBlank()) {
            return;
        }

        host = host.trim();
        int puerto = 5000;

        try {
            Sesion.conectar(host, puerto);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con el Backend en " + host + ":" + puerto,
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame ventana = new LoginFrame();
            new LoginController(ventana, null);
            ventana.setVisible(true);
        });
    }
}
