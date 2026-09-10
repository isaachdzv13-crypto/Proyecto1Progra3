package util;

import javax.swing.*;
import java.awt.print.PrinterException;

public final class Impresora {

    private Impresora() {
    }

    public static void imprimirTabla(JComponent padre, JTable tabla, String titulo) {
        try {
            boolean seCompleto = tabla.print(JTable.PrintMode.FIT_WIDTH, null, null);
            if (!seCompleto) {
                JOptionPane.showMessageDialog(padre, "Impresion cancelada.");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(padre,
                    "No se pudo imprimir " + titulo + ": " + ex.getMessage(),
                    "Error de impresion", JOptionPane.ERROR_MESSAGE);
        }
    }
}
