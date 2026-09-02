package Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class Themes {

    // Colores de la aplicación
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color TEXT = new Color(30, 41, 59);

    private Themes() {
    }

    // Estilo general
    public static void install() {
        UIManager.put(
                "Label.font",
                new Font("SansSerif", Font.PLAIN, 14)
        );

        UIManager.put(
                "Button.font",
                new Font("SansSerif", Font.BOLD, 13)
        );

        UIManager.put(
                "TextField.font",
                new Font("SansSerif", Font.PLAIN, 14)
        );

        UIManager.put(
                "PasswordField.font",
                new Font("SansSerif", Font.PLAIN, 14)
        );
    }

    // Crear botones con el mismo estilo
    public static JButton button(String texto, Color color) {

        JButton boton = new JButton(texto);

        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorder(
                new EmptyBorder(10, 16, 10, 16)
        );

        return boton;
    }

    // Crear campos de texto con el mismo estilo
    public static JTextField textField() {

        JTextField campo = new JTextField();

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)
                        ),
                        new EmptyBorder(5, 8, 5, 8)
                )
        );

        return campo;
    }
}
