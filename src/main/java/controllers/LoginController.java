package controllers;

import contexto.DatosQuemados;
import model.Administrador;
import model.Funcionario;
import model.Usuario;
import view.FrameAdminPrincipal;
import view.FrameFuncionarioPrincipal;
import view.LoginFrame;
import view.PasswordChangeFrame;

import javax.swing.*;
import java.util.Objects;

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
            new FrameFuncionarioPrincipal((Funcionario) usuario).setVisible(true);
            vista.dispose();

        }
    }
//aa
    private Usuario validarCredenciales(String id, String clave) {
        return DatosQuemados.getInstancia().login(id, clave);
    }
    private void abrirCambiarClave() {
        PasswordChangeFrame ventana = new PasswordChangeFrame();

        ventana.aceptar.addActionListener(e -> cambiarContrasenia(ventana));
        ventana.cancelar.addActionListener(e -> ventana.dispose());

        ventana.setVisible(true);
    }
    private void cambiarContrasenia( PasswordChangeFrame v){
      String claveVieja= v.txtClave.getText();
        String claveNueva= v.txtClaveNueva.getText();
        String claveNueva2= v.txtClaveNueva2.getText();
        if ( claveVieja.isBlank()
                || claveNueva.isBlank() || claveNueva2.isBlank()) {
            mostrarError("Los campos no pueden estar vacíos.");
            return;
        }

        if (!claveNueva.equals(claveNueva2)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }
        Funcionario aux= DatosQuemados.getInstancia().getFuncionarios().buscarPorContrasenia(claveVieja);
        if(aux==null){
            mostrarError("No se encontro un usuario con esa contraseña...");
            return;
        }
        aux.setClave(claveNueva);
        DatosQuemados.getInstancia().guardar();
JOptionPane.showMessageDialog(v,"Cambio exitoso");

        v.dispose();

    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
