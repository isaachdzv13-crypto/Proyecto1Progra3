package controllers;

import model.Administrador;
import model.Funcionario;
import model.Usuario;
import view.LoginFrame;

import javax.swing.*;

public class LoginController {
    private final LoginFrame vista;

    public LoginController(LoginFrame view) {
        this.vista = view;
        view.iniciar.addActionListener(e -> {
            String id = view.getId();
            String clave = view.getClave();

            iniciarSesion(id, clave);
        });
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
            // new AdminFrame((Administrador) usuario).setVisible(true);




        } else {

            JOptionPane.showMessageDialog(vista,"\"Bienvenido funcionario.\"");

            // Abrir ventana del funcionario
            // new FuncionarioFrame((Funcionario) usuario).setVisible(true);
        }
    }

    private Usuario validarCredenciales(String id, String clave) {

        Administrador admin =
                new Administrador("admin", "1234");

        Funcionario funcionario =
                new Funcionario( "1234", "Juan", "8888-8888");

        if (id.equals(admin.getId()) &&
                clave.equals(admin.getClave())) {

            return admin;
        }

        if (id.equals(funcionario.getId()) &&
                clave.equals(funcionario.getClave())) {

            return funcionario;
        }

        return null;
    }
}
