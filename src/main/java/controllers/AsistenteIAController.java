package controllers;

import contexto.DatosQuemados;
import ia.AsistenteReservaIA;
import ia.ConfiguracionIA;
import model.CategoriaRecurso;
import repository.ListaCategorias;
import view.AsistenteIAPanel;
import view.ReservasPanel;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AsistenteIAController {

    private final AsistenteIAPanel vista;
    private final ReservasPanel vistaReservas;
    private final JTabbedPane tabs;
    private final int indiceTabReservas;
    private final ListaCategorias categorias = DatosQuemados.getInstancia().getCategorias();
    private final AsistenteReservaIA asistente = new AsistenteReservaIA();

    private AsistenteReservaIA.SugerenciaReserva ultimaSugerencia;
    private CategoriaRecurso categoriaEncontrada;

    public AsistenteIAController(AsistenteIAPanel vista, ReservasPanel vistaReservas,
                                  JTabbedPane tabs, int indiceTabReservas) {
        this.vista = vista;
        this.vistaReservas = vistaReservas;
        this.tabs = tabs;
        this.indiceTabReservas = indiceTabReservas;

        vista.btnInterpretar.addActionListener(e -> interpretar());
        vista.btnUsarEnFormulario.addActionListener(e -> usarEnFormulario());
    }

    private void interpretar() {
        String solicitud = vista.txtSolicitud.getText().trim();
        if (solicitud.isBlank()) {
            mostrarError("Escriba lo que necesita reservar.");
            return;
        }

        String apiKey = ConfiguracionIA.obtenerApiKey();
        if (apiKey == null) {
            mostrarError("No se encontro la llave de la API. Configure la variable de entorno "
                    + "ANTHROPIC_API_KEY o cree un archivo apikey.txt en la raiz del proyecto.");
            return;
        }

        vista.btnInterpretar.setEnabled(false);
        vista.btnInterpretar.setText("Interpretando...");

        SwingWorker<AsistenteReservaIA.SugerenciaReserva, Void> tarea = new SwingWorker<>() {
            @Override
            protected AsistenteReservaIA.SugerenciaReserva doInBackground() throws Exception {
                return asistente.interpretar(solicitud, apiKey);
            }

            @Override
            protected void done() {
                vista.btnInterpretar.setEnabled(true);
                vista.btnInterpretar.setText("Interpretar con IA");
                try {
                    mostrarSugerencia(get());
                } catch (Exception ex) {
                    mostrarError("No se pudo interpretar la solicitud: " + causaLegible(ex));
                }
            }
        };
        tarea.execute();
    }

    private void mostrarSugerencia(AsistenteReservaIA.SugerenciaReserva sugerencia) {
        ultimaSugerencia = sugerencia;
        categoriaEncontrada = buscarCategoria(sugerencia.categoria());

        vista.lblActividad.setText(sugerencia.actividad());
        vista.lblFecha.setText(sugerencia.fecha());
        vista.lblHorario.setText(sugerencia.horaInicio() + " - " + sugerencia.horaFin());
        vista.lblCategoria.setText(categoriaEncontrada != null
                ? categoriaEncontrada.getDesc()
                : sugerencia.categoria() + " (no encontrada, seleccionela manualmente)");

        vista.btnUsarEnFormulario.setEnabled(true);
    }

    private void usarEnFormulario() {
        if (ultimaSugerencia == null) return;

        vistaReservas.txtActividad.setText(ultimaSugerencia.actividad());

        try {
            LocalDate.parse(ultimaSugerencia.fecha());
            vistaReservas.txtFecha.setText(ultimaSugerencia.fecha());
        } catch (DateTimeParseException ex) {
            vistaReservas.txtFecha.setText(LocalDate.now().plusDays(1).toString());
        }

        seleccionarSiExiste(vistaReservas.cmbHoraInicio, ultimaSugerencia.horaInicio());
        seleccionarSiExiste(vistaReservas.cmbHoraFin, ultimaSugerencia.horaFin());

        vistaReservas.listaCategorias.clearSelection();
        if (categoriaEncontrada != null) {
            vistaReservas.listaCategorias.setSelectedValue(categoriaEncontrada, true);
        }

        tabs.setSelectedIndex(indiceTabReservas);
    }

    private void seleccionarSiExiste(JComboBox<String> combo, String valor) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(valor)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private CategoriaRecurso buscarCategoria(String textoCategoria) {
        if (textoCategoria == null || textoCategoria.equalsIgnoreCase("NULL")) return null;

        String buscado = textoCategoria.toLowerCase();
        List<CategoriaRecurso> todas = categorias.listarTodas();

        for (CategoriaRecurso categoria : todas) {
            if (categoria.getDesc().toLowerCase().contains(buscado)
                    || buscado.contains(categoria.getDesc().toLowerCase())) {
                return categoria;
            }
        }
        return null;
    }

    private String causaLegible(Exception ex) {
        Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
        return causa.getMessage() != null ? causa.getMessage() : causa.toString();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
