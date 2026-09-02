package view;

import Theme.Themes;
import controllers.LoginController;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
private JTextField id;
private JPasswordField password;
    public final JCheckBox chkMostrar = new JCheckBox("Mostrar contraseña");
    public JButton iniciar = Themes.button(
            "Iniciar sesión",
            Themes.PRIMARY
    );



    public JButton limpiar= Themes.button("Limpiar",Themes.PRIMARY);

    public JButton cambiarClave=Themes.button("Cambiar",Themes.PRIMARY);


    public LoginFrame(){
        iniciar.setBounds(180, 225, 120, 40);
        limpiar.setBounds(40, 225, 120, 40);
        cambiarClave.setBounds(320,225,120,40);

        limpiar.addActionListener(e -> {
            id.setText("");
            password.setText("");
        });


    setTitle("Sistema de Reserva de Recursos - Inicio de sesión");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(500, 350);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel login = new JPanel();
    login.setLayout(null);

    // Bienvenido
    JLabel entrada = new JLabel("Bienvenido");
    entrada.setFont(
            entrada.getFont().deriveFont(Font.BOLD, 19f)
    );
    entrada.setHorizontalAlignment(SwingConstants.CENTER);
    entrada.setBounds(100, 15, 300, 30);

    // Mensaje
    JLabel saludo = new JLabel(
            "Digite sus credenciales para ingresar."
    );
    saludo.setHorizontalAlignment(SwingConstants.CENTER);
    saludo.setBounds(75, 50, 350, 30);

    // ID
    JLabel lblId = new JLabel("ID:");
    lblId.setBounds(100, 105, 80, 25);

    id = Themes.textField();
    id.setBounds(180, 105, 220, 30);

    // Contraseña
    JLabel lblPassword = new JLabel("Contraseña:");
    lblPassword.setBounds(100, 150, 80, 25);

    password = new JPasswordField();
    password.setBounds(180, 150, 220, 30);
    chkMostrar.setBounds(180,185,200,20);
        chkMostrar.addActionListener(e -> {
            if (chkMostrar.isSelected()) {
                password.setEchoChar((char) 0);
            } else {
                password.setEchoChar('•');
            }
        });


    // Botón

JLabel demo= new JLabel("Admin: admin/1234. Usuario: 1234/1234");
demo.setBounds(180,275,300,40);
login.add(demo);
    // Agregar
    login.add(entrada);
    login.add(saludo);
    login.add(lblId);
    login.add(id);
    login.add(lblPassword);
    login.add(password);
    login.add(iniciar);
        login.add(limpiar);
        login.add(cambiarClave);
login.add(chkMostrar);
    add(login);
}





    public static void main(String[] args) {
        Themes.install();

        SwingUtilities.invokeLater(() -> {
            LoginFrame ventana = new LoginFrame();
            new LoginController(ventana,null);
            ventana.setVisible(true);
        });
    }

    public String getId() {
        return id.getText();
    }

    public String getClave() {
        return new String(password.getPassword());
    }
}
