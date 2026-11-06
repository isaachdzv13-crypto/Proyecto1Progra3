package logica;

import datos.CategoriaDAO;
import datos.RecursoDAO;
import datos.ReservaDAO;
import datos.UsuarioDAO;
import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import model.Usuario;
import red.ServiceException;
import service.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceMySQL implements Service {

    private final UsuarioDAO usuarios = new UsuarioDAO();
    private final CategoriaDAO categorias = new CategoriaDAO();
    private final RecursoDAO recursos = new RecursoDAO();
    private final ReservaDAO reservas = new ReservaDAO();

    @Override
    public Usuario login(String id, String clave) {
        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            return null;
        }
        Usuario usuario = usuarios.buscarPorId(id.trim());
        if (usuario != null && usuario.getClave().equals(clave)) {
            return usuario;
        }
        return null;
    }

    @Override
    public void cerrarSesion() {
    }

    @Override
    public synchronized void cambiarClave(String id, String claveActual, String claveNueva) {
        if (esVacio(id) || esVacio(claveActual) || esVacio(claveNueva)) {
            throw new ServiceException("Los campos no pueden estar vacíos.");
        }
        Usuario usuario = usuarios.buscarPorId(id.trim());
        if (usuario == null || !usuario.getClave().equals(claveActual)) {
            throw new ServiceException("El ID o la clave actual son incorrectos.");
        }
        usuarios.actualizarClave(usuario.getId(), claveNueva);
    }

    @Override
    public List<Funcionario> listarFuncionarios() {
        return usuarios.listarFuncionarios();
    }

    @Override
    public Funcionario buscarFuncionario(String id) {
        if (esVacio(id)) {
            return null;
        }
        return usuarios.buscarFuncionario(id.trim());
    }

    @Override
    public List<Funcionario> buscarFuncionariosPorNombre(String nombre) {
        return usuarios.buscarPorNombre(nombre);
    }

    @Override
    public synchronized void agregarFuncionario(Funcionario f) {
        validarFuncionario(f);
        if (usuarios.buscarPorId(f.getId()) != null) {
            throw new ServiceException("Ya existe un usuario con el ID " + f.getId() + ".");
        }
        usuarios.insertarFuncionario(f);
    }

    @Override
    public synchronized void actualizarFuncionario(Funcionario f) {
        validarFuncionario(f);
        if (usuarios.buscarFuncionario(f.getId()) == null) {
            throw new ServiceException("No existe el funcionario " + f.getId() + ".");
        }
        usuarios.actualizarFuncionario(f);
    }

    @Override
    public synchronized void borrarFuncionario(String id) {
        if (usuarios.buscarFuncionario(id) == null) {
            throw new ServiceException("No existe el funcionario " + id + ".");
        }
        if (reservas.contarPorFuncionario(id) > 0) {
            throw new ServiceException("No se puede borrar: el funcionario tiene reservas registradas.");
        }
        usuarios.borrarFuncionario(id);
    }

    @Override
    public List<CategoriaRecurso> listarCategorias() {
        return categorias.listar();
    }

    @Override
    public CategoriaRecurso buscarCategoria(String id) {
        if (esVacio(id)) {
            return null;
        }
        return categorias.buscarPorId(id);
    }

    @Override
    public List<CategoriaRecurso> buscarCategoriasPorDescripcion(String desc) {
        return categorias.buscarPorDescripcion(desc);
    }

    @Override
    public synchronized CategoriaRecurso agregarCategoria(String desc) {
        if (esVacio(desc)) {
            throw new ServiceException("La descripción es obligatoria.");
        }
        return categorias.insertar(desc.trim());
    }

    @Override
    public synchronized void actualizarCategoria(CategoriaRecurso c) {
        if (c == null || esVacio(c.getDesc())) {
            throw new ServiceException("La descripción es obligatoria.");
        }
        if (categorias.buscarPorId(c.getId()) == null) {
            throw new ServiceException("No existe la categoría " + c.getId() + ".");
        }
        c.setDesc(c.getDesc().trim());
        categorias.actualizar(c);
    }

    @Override
    public synchronized void borrarCategoria(String id) {
        if (categorias.buscarPorId(id) == null) {
            throw new ServiceException("No existe la categoría " + id + ".");
        }
        if (recursos.contarPorCategoria(id) > 0) {
            throw new ServiceException("No se puede borrar: hay recursos asociados a esta categoría.");
        }
        categorias.borrar(id);
    }

    @Override
    public List<Recurso> listarRecursos() {
        return recursos.listar();
    }

    @Override
    public Recurso buscarRecurso(String id) {
        if (esVacio(id)) {
            return null;
        }
        return recursos.buscarPorId(id);
    }

    @Override
    public List<Recurso> buscarRecursos(String idCategoria, String descripcion) {
        return recursos.buscar(idCategoria, descripcion);
    }

    @Override
    public synchronized void agregarRecurso(Recurso r) {
        validarRecurso(r);
        if (recursos.buscarPorId(r.getId()) != null) {
            throw new ServiceException("Ya existe un recurso con el ID " + r.getId() + ".");
        }
        recursos.insertar(r);
    }

    @Override
    public synchronized void actualizarRecurso(Recurso r) {
        validarRecurso(r);
        if (recursos.buscarPorId(r.getId()) == null) {
            throw new ServiceException("No existe el recurso " + r.getId() + ".");
        }
        recursos.actualizar(r);
    }

    @Override
    public synchronized void borrarRecurso(String id) {
        if (recursos.buscarPorId(id) == null) {
            throw new ServiceException("No existe el recurso " + id + ".");
        }
        if (recursos.contarReservas(id) > 0) {
            throw new ServiceException("No se puede borrar: el recurso está asociado a reservas.");
        }
        recursos.borrar(id);
    }

    @Override
    public List<Reserva> listarReservas() {
        return reservas.listarTodas();
    }

    @Override
    public List<Reserva> listarReservasPorFuncionario(String idFuncionario) {
        return reservas.listarPorFuncionario(idFuncionario);
    }

    @Override
    public synchronized Reserva crearReserva(String idFuncionario, String actividad, LocalDate fecha,
                                             LocalTime inicio, LocalTime fin, List<String> idsCategorias) {
        if (esVacio(actividad)) {
            throw new ServiceException("La actividad es obligatoria.");
        }
        if (fecha == null || inicio == null || fin == null) {
            throw new ServiceException("La fecha y las horas son obligatorias.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new ServiceException("La fecha no puede ser anterior a hoy.");
        }
        if (!fin.isAfter(inicio)) {
            throw new ServiceException("La hora final debe ser posterior a la hora de inicio.");
        }
        if (idsCategorias == null || idsCategorias.isEmpty()) {
            throw new ServiceException("Seleccione al menos una categoria.");
        }
        if (esVacio(idFuncionario) || usuarios.buscarFuncionario(idFuncionario) == null) {
            throw new ServiceException("El funcionario no existe.");
        }

        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (String idCategoria : idsCategorias) {
            CategoriaRecurso categoria = categorias.buscarPorId(idCategoria);
            if (categoria == null) {
                throw new ServiceException("No existe la categoría " + idCategoria + ".");
            }
            Recurso disponible = buscarPrimerDisponible(idCategoria, fecha, inicio, fin);
            if (disponible == null) {
                noDisponibles.add(categoria.getDesc());
            } else {
                asignados.add(disponible);
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new ServiceException("No hay recursos disponibles para: " + String.join(", ", noDisponibles));
        }

        String actividadLimpia = actividad.trim();
        Reserva reserva = new Reserva(idFuncionario, reservas.siguienteId(), actividadLimpia,
                actividadLimpia, inicio, fin, fecha);
        reserva.setRecursos(asignados);
        reservas.insertar(reserva);
        return reserva;
    }

    @Override
    public synchronized void cancelarReserva(String idReserva, String idFuncionario) {
        Reserva reserva = reservas.buscarPorId(idReserva);

        if (reserva == null) {
            throw new ServiceException("No existe esa reserva");
        }
        if (!reserva.getIdFuncionario().equals(idFuncionario)) {
            throw new ServiceException("Solo el dueño de la reserva puede cancelarla");
        }
        if (reserva.getEstado() == Reserva.Estado.CANCELADA) {
            throw new ServiceException("Esa reserva ya estaba cancelada");
        }
        if (!reserva.getFecha().isAfter(LocalDate.now())) {
            throw new ServiceException("Solo se pueden cancelar reservas futuras");
        }
        reservas.cambiarEstado(idReserva, Reserva.Estado.CANCELADA);
    }

    private Recurso buscarPrimerDisponible(String idCategoria, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Recurso recurso : recursos.buscar(idCategoria, "")) {
            if (!reservas.recursoOcupado(recurso.getId(), fecha, inicio, fin)) {
                return recurso;
            }
        }
        return null;
    }

    private void validarFuncionario(Funcionario f) {
        if (f == null || esVacio(f.getId())) {
            throw new ServiceException("El ID es obligatorio.");
        }
        if (esVacio(f.getNombre())) {
            throw new ServiceException("El nombre es obligatorio.");
        }
        if (esVacio(f.getTelefono())) {
            throw new ServiceException("El telefono es obligatorio.");
        }
    }

    private void validarRecurso(Recurso r) {
        if (r == null || esVacio(r.getId())) {
            throw new ServiceException("El ID es obligatorio.");
        }
        if (r.getCategoria() == null || categorias.buscarPorId(r.getCategoria().getId()) == null) {
            throw new ServiceException("Debe seleccionar una categoria válida.");
        }
        if (esVacio(r.getDescripcion())) {
            throw new ServiceException("La descripcion es obligatoria.");
        }
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
