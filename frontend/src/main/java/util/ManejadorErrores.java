package util;

import red.ServiceException;

import javax.swing.*;
import java.awt.Component;

public class ManejadorErrores {

    public static void ejecutar(Component padre, Runnable accion) {
        try {
            accion.run();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(padre, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
