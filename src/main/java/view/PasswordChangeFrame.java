package view;

import Theme.Themes;

import javax.swing.*;
import java.awt.*;

public class PasswordChangeFrame extends JFrame {
    public JTextField txtClave;
    public JTextField txtClaveNueva;
    public JTextField txtClaveNueva2;



    public JButton aceptar= Themes.button("Aceptar",Themes.PRIMARY);
    public JButton cancelar= Themes.button("Cancelar",Themes.PRIMARY);


    public PasswordChangeFrame(){

        aceptar.setBounds(200,150,100,20);
        cancelar.setBounds(50,150,100,20);
        cancelar.addActionListener(e->dispose());
        setTitle("Sistema de Reserva de Recursos - Cambio de contraseña");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        ImageIcon image= new ImageIcon("logo.png");
        Image imagen= image.getImage();
        Image imagenRedimensionada= imagen.getScaledInstance(32,32,Image.SCALE_SMOOTH);
        this.setIconImage(imagenRedimensionada);

        JPanel panel=new JPanel();
        panel.setLayout(null);

        JLabel txtContrasenia1= new JLabel("Clave actual");
        txtContrasenia1.setBounds(30,20,120,25);
        txtClave=Themes.textField();
        txtClave.setBounds(150,20,200,30);


        JLabel txtContrasenia2= new JLabel("Clave nueva");
        txtContrasenia2.setBounds(30,60,120,30);
        txtClaveNueva=Themes.textField();
        txtClaveNueva.setBounds(150,60,200,30);

        JLabel txtContrasenia3= new JLabel("Confirme la clave");
        txtContrasenia3.setBounds(30,100,120,30);
        txtClaveNueva2=Themes.textField();
        txtClaveNueva2.setBounds(150,100,200,30);


        panel.add(txtContrasenia1);
        panel.add(txtContrasenia2);
        panel.add(txtContrasenia3);
        panel.add(txtClave);
        panel.add(txtClaveNueva);
        panel.add(txtClaveNueva2);
        panel.add(aceptar);
        panel.add(cancelar);
        add(panel);

    }
public String getContraNueva(){
        return this.txtClaveNueva.getText().trim();
}
    public String getContraNueva2() {
        return this.txtClaveNueva2.getText().trim();

    }
}
