package util;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class GeneradorPDF {

    public static void crearPDF(
            JTable tabla,
            String titulo
    ) {
        Frame ventana =
                (Frame) SwingUtilities.getWindowAncestor(tabla);

        FileDialog selector = new FileDialog(
                ventana,
                "Guardar PDF",
                FileDialog.SAVE
        );

        selector.setFile("reporte.pdf");
        selector.setVisible(true);

        if (selector.getFile() == null) {
            return;
        }

        File archivo = new File(
                selector.getDirectory(),
                selector.getFile()
        );

        String ruta = archivo.getAbsolutePath();
        if (!ruta.endsWith(".pdf")) {
            ruta = ruta + ".pdf";
        }

        try {
            Document documento = new Document();

            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream(ruta)
            );

            documento.open();
            documento.add(new Paragraph(titulo));
            documento.add(new Paragraph(" "));

            PdfPTable tablaPDF =
                    new PdfPTable(tabla.getColumnCount());

            for (int columna = 0;
                 columna < tabla.getColumnCount();
                 columna++) {

                tablaPDF.addCell(
                        tabla.getColumnName(columna)
                );
            }

            for (int fila = 0;
                 fila < tabla.getRowCount();
                 fila++) {

                for (int columna = 0;
                     columna < tabla.getColumnCount();
                     columna++) {

                    Object valor =
                            tabla.getValueAt(fila, columna);

                    String texto = "";

                    if (valor != null) {
                        texto = valor.toString();
                    }

                    tablaPDF.addCell(texto);
                }
            }

            documento.add(tablaPDF);
            documento.close();

            JOptionPane.showMessageDialog(
                    tabla,
                    "PDF creado correctamente"
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    tabla,
                    "No se pudo crear el PDF"
            );
        }
    }
}