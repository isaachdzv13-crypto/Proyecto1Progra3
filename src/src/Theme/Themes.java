package Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
public final class Themes {

    // Colores de la aplicación
    public static final Color PRIMARY = new Color(59, 130, 246);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color SUCCESS = new Color(34, 197, 94);

    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color BACKGROUND = new Color(248, 250, 252);
    public static final Color BORDER = new Color(203, 213, 225);
    public static final Color TABLE_HEADER = new Color(241, 245, 249);

    private Themes() {
    }


    // =========================
    // ESTILO GENERAL
    // =========================

    public static void install() {

        UIManager.put(
                "Panel.background",
                BACKGROUND
        );

        UIManager.put(
                "Label.foreground",
                TEXT
        );

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

        UIManager.put(
                "Table.font",
                new Font("SansSerif", Font.PLAIN, 13)
        );

        UIManager.put(
                "TableHeader.font",
                new Font("SansSerif", Font.BOLD, 13)
        );
    }


    // =========================
    // BOTONES
    // =========================

    public static JButton button(String texto, Color color) {

        JButton boton = new JButton(texto);

        boton.setForeground(Color.WHITE);
        boton.setBackground(color);

        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);

        boton.setBorder(
                new EmptyBorder(8, 14, 8, 14)
        );

        return boton;
    }


    // =========================
    // CAMPOS DE TEXTO
    // =========================

    public static JTextField textField() {

        JTextField campo = new JTextField();

        campo.setForeground(TEXT);
        campo.setBackground(Color.WHITE);

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                5, 8, 5, 8
                        )
                )
        );

        return campo;
    }
}