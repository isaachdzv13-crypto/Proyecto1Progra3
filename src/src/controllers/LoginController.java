package controllers;

import contexto.DatosQuemados;
import model.Administrador;
import model.Funcionario;
import model.Usuario;
import view.FrameAdminPrincipal;
import view.LoginFrame;
import view.PasswordChangeFrame;

import javax.swing.*;

public class LoginController {
    private final LoginFrame vista;
    private final PasswordChangeFrame cambioContra;
    public LoginController(LoginFrame view, PasswordChangeFrame cambioContra) {
        this.vista = view;
        this.cambioContra = cambioContra;
        view.iniciar.addActionListener(e -> {
            String id = view.getId();
            String clave = view.getClave();

            iniciarSesion(id, clave);
        });
        view.cambiarClave.addActionListener(e->abrirCambiarClave());


    }


    public void iniciarSesion(String id, String clave) {

        if (id == null || id.isBlank()) {
            JOptionPane.showMessageDialog(vista,"Debe ingresar su ID.");

            return;
        }

        if (clave == null || clave.isBlank()) {
            JOptionPane.showMessageDialog(vista,"Debe ingresar su contraseña.");
            return;
        }

        Usuario usuario = validarCredenciales(id, clave);

        if (usuario == null) {

            JOptionPane.showMessageDialog(vista,"ID o contraseña incorrectos.");
            return;
        }

        if (usuario.getRol() == Usuario.Rol.ADMINISTRADOR) {

            JOptionPane.showMessageDialog(vista,"Bienvenido administrador.");

            // Abrir ventana del administrador
             new FrameAdminPrincipal((Administrador) usuario).setVisible(true);
            vista.dispose();



        } else {

            JOptionPane.showMessageDialog(vista,"\"Bienvenido funcionario.\"");

            // Abrir ventana del funcionario
            // new FuncionarioFrame((Funcionario) usuario).setVisible(true);
        }
    }
//aa
    private Usuario validarCredenciales(String id, String clave) {
        return DatosQuemados.getInstancia().login(id, clave);
    }
    private void abrirCambiarClave() {
        PasswordChangeFrame ventana = new PasswordChangeFrame();
        ventana.setVisible(true);
    }
}
