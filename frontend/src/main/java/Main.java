import Theme.Themes;
import contexto.Sesion;
import controllers.LoginController;
import view.LoginFrame;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Themes.install();

        String servidor = "localhost:5000";
        if (args.length > 0) {
            servidor = args[0];
        } else {
            String respuesta = JOptionPane.showInputDialog(null,
                    "Servidor (host:puerto)", "Conexion al Backend", JOptionPane.QUESTION_MESSAGE);
            if (respuesta == null) {
                return;
            }
            if (!respuesta.isBlank()) {
                servidor = respuesta.trim();
            }
        }

        String host = servidor;
        int puerto = 5000;
        if (servidor.contains(":")) {
            host = servidor.substring(0, servidor.indexOf(":"));
            try {
                puerto = Integer.parseInt(servidor.substring(servidor.indexOf(":") + 1).trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El puerto no es valido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

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
