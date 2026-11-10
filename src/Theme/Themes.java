package Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
public final class Themes {

    // Paleta oscura de la aplicacion
    public static final Color PRIMARY = new Color(59, 130, 246);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color SUCCESS = new Color(22, 163, 74);

    public static final Color TEXT = new Color(226, 232, 240);
    public static final Color TEXT_DISABLED = new Color(148, 163, 184);
    public static final Color BACKGROUND = new Color(15, 23, 42);
    public static final Color SURFACE = new Color(30, 41, 59);
    public static final Color INPUT = new Color(51, 65, 85);
    public static final Color BORDER = new Color(71, 85, 105);
    public static final Color TABLE_HEADER = new Color(30, 41, 59);
    public static final Color SELECTION = new Color(37, 99, 235);

    private Themes() {
    }

    public static void install() {
        Font normal = new Font("SansSerif", Font.PLAIN, 14);
        Font small = new Font("SansSerif", Font.PLAIN, 13);
        Font bold = new Font("SansSerif", Font.BOLD, 13);

        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Viewport.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);

        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.font", normal);
        UIManager.put("CheckBox.background", BACKGROUND);
        UIManager.put("CheckBox.foreground", TEXT);

        UIManager.put("Button.font", bold);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.background", SURFACE);

        UIManager.put("TextField.font", normal);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.background", INPUT);
        UIManager.put("TextField.caretForeground", TEXT);
        UIManager.put("TextField.inactiveForeground", TEXT_DISABLED);
        UIManager.put("TextField.inactiveBackground", SURFACE);

        UIManager.put("PasswordField.font", normal);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.background", INPUT);
        UIManager.put("PasswordField.caretForeground", TEXT);

        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.background", INPUT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", SELECTION);

        UIManager.put("Table.font", small);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.background", SURFACE);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.selectionBackground", SELECTION);

        UIManager.put("TableHeader.font", bold);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TableHeader.background", TABLE_HEADER);

        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.background", BACKGROUND);
        UIManager.put("TabbedPane.selected", SURFACE);

        UIManager.put("TitledBorder.titleColor", TEXT);
        UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(BORDER));
        UIManager.put("TitledBorder.font", bold);

        UIManager.put("ScrollPane.background", BACKGROUND);
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDER));
        UIManager.put("OptionPane.messageForeground", TEXT);
    }

    public static JButton button(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setBorder(new EmptyBorder(8, 14, 8, 14));
        return boton;
    }

    public static JTextField textField() {
        JTextField campo = new JTextField();
        campo.setForeground(TEXT);
        campo.setBackground(INPUT);
        campo.setCaretColor(TEXT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return campo;
    }
}