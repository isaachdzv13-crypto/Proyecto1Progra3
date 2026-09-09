package controllers;

import contexto.DatosQuemados;
import model.CategoriaRecurso;
import model.Recurso;
import model.Reserva;
import view.EstadisticasPanel;

import java.util.List;

public class EstadisticasController {
    private final EstadisticasPanel vista;

    public EstadisticasController(EstadisticasPanel vista) {
        this.vista = vista;
        vista.btnActualizar.addActionListener(e -> actualizar());
        actualizar();
    }

    public void actualizar() {
        DatosQuemados datos = DatosQuemados.getInstancia();

        int totalFuncionarios = datos.getFuncionarios().listarTodos().size();
        int totalCategorias = datos.getCategorias().listarTodas().size();
        int totalRecursos = datos.getRecursos().listarTodos().size();

        List<Reserva> reservas = datos.getReservas().listarTodas();
        long reservasActivas = reservas.stream()
                .filter(r -> r.getEstado() == Reserva.Estado.ACTIVA)
                .count();
        long reservasCanceladas = reservas.size() - reservasActivas;

        vista.lblFuncionarios.setText(String.valueOf(totalFuncionarios));
        vista.lblCategorias.setText(String.valueOf(totalCategorias));
        vista.lblRecursos.setText(String.valueOf(totalRecursos));
        vista.lblReservasActivas.setText(String.valueOf(reservasActivas));
        vista.lblReservasCanceladas.setText(String.valueOf(reservasCanceladas));

        vista.modeloRecursosPorCategoria.setRowCount(0);
        for (CategoriaRecurso categoria : datos.getCategorias().listarTodas()) {
            List<Recurso> recursosDeCategoria = datos.getRecursos().filtrarPorCategoria(categoria);
            vista.modeloRecursosPorCategoria.addRow(new Object[]{
                    categoria.getDesc(),
                    recursosDeCategoria.size()
            });
        }
    }
}
