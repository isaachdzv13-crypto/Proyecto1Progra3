package util;

import javax.swing.*;
import java.awt.print.PrinterException;

/**
 * Utilidad simple para imprimir el contenido de una tabla.
 *
 * Usa la funcion de impresion que ya trae JTable (JTable.print). Esto abre
 * el dialogo de impresion normal del sistema operativo, donde el usuario
 * puede elegir una impresora fisica o una opcion como "Guardar como PDF" /
 * "Microsoft Print to PDF" para generar el reporte en PDF sin necesidad de
 * agregar ninguna libreria extra al proyecto.
 */
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
