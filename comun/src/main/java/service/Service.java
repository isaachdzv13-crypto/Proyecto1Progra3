package service;

import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import model.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface Service {

    Usuario login(String id, String clave);

    void cerrarSesion();

    void cambiarClave(String id, String claveActual, String claveNueva);

    List<Funcionario> listarFuncionarios();

    Funcionario buscarFuncionario(String id);

    List<Funcionario> buscarFuncionariosPorNombre(String nombre);

    void agregarFuncionario(Funcionario funcionario);

    void actualizarFuncionario(Funcionario funcionario);

    void borrarFuncionario(String id);

    List<CategoriaRecurso> listarCategorias();

    CategoriaRecurso buscarCategoria(String id);

    List<CategoriaRecurso> buscarCategoriasPorDescripcion(String desc);

    CategoriaRecurso agregarCategoria(String desc);

    void actualizarCategoria(CategoriaRecurso categoria);

    void borrarCategoria(String id);

    List<Recurso> listarRecursos();

    Recurso buscarRecurso(String id);

    List<Recurso> buscarRecursos(String idCategoria, String descripcion);

    void agregarRecurso(Recurso recurso);

    void actualizarRecurso(Recurso recurso);

    void borrarRecurso(String id);

    List<Reserva> listarReservas();

    List<Reserva> listarReservasPorFuncionario(String idFuncionario);

    Reserva crearReserva(String idFuncionario, String actividad, LocalDate fecha,
                         LocalTime inicio, LocalTime fin, List<String> idsCategorias);

    void cancelarReserva(String idReserva, String idFuncionario);
}
