package logica;

import accesoDatos.ServicioCategoria;
import accesoDatos.ServicioFuncionarios;
import accesoDatos.ServicioLogin;
import accesoDatos.ServicioRecurso;
import accesoDatos.ServicioReserva;
import accesoDatos.ServicioUsuario;
import model.Administrador;
import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import model.Usuario;
import red.ServiceException;
import service.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceReal implements Service {

    private interface Operacion<T> {
        T ejecutar() throws SQLException, ClassNotFoundException;
    }

    private final ServicioLogin login = new ServicioLogin();
    private final ServicioUsuario usuarios = new ServicioUsuario();
    private final ServicioFuncionarios funcionarios = new ServicioFuncionarios();
    private final ServicioCategoria categorias = new ServicioCategoria();
    private final ServicioRecurso recursos = new ServicioRecurso();
    private final ServicioReserva reservas = new ServicioReserva();

    private <T> T ejecutar(Operacion<T> operacion) {
        try {
            return operacion.ejecutar();
        } catch (SQLException e) {
            throw traducir(e);
        } catch (ClassNotFoundException e) {
            throw new ServiceException("No se encontró el driver de la base de datos.", e);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    static ServiceException traducir(SQLException e) {
        int codigo = e.getErrorCode();
        String mensaje = e.getMessage() == null ? "" : e.getMessage();

        if (codigo >= 20000 && codigo <= 20999) {
            String limpio = mensaje.split("\n")[0];
            if (limpio.startsWith("ORA-") && limpio.contains(": ")) {
                limpio = limpio.substring(limpio.indexOf(": ") + 2);
            }
            return new ServiceException(limpio, e);
        }
        if (codigo == 2292) {
            return new ServiceException("No se puede borrar: hay registros asociados.", e);
        }
        if (codigo == 2291) {
            return new ServiceException("Hay un dato relacionado que no existe (categoría, recurso o funcionario).", e);
        }
        if (codigo == 1) {
            return new ServiceException("Ya existe un registro con ese identificador.", e);
        }
        return new ServiceException("Error en la base de datos: " + mensaje, e);
    }

    @Override
    public Usuario login(String id, String clave) {
        if (esVacio(id) || esVacio(clave)) {
            return null;
        }
        return ejecutar(() -> {
            String rol = login.login(id.trim(), clave);
            if (rol == null) {
                return null;
            }
            if ("FUNCIONARIO".equals(rol)) {
                return funcionarios.buscarPorId(id.trim());
            }
            return new Administrador(id.trim(), clave);
        });
    }

    @Override
    public void cerrarSesion() {
    }

    @Override
    public synchronized void cambiarClave(String id, String claveActual, String claveNueva) {
        if (esVacio(id) || esVacio(claveActual) || esVacio(claveNueva)) {
            throw new ServiceException("Los campos no pueden estar vacíos.");
        }
        ejecutar(() -> {
            usuarios.cambiarClave(id.trim(), claveActual, claveNueva);
            return null;
        });
    }

    @Override
    public List<Funcionario> listarFuncionarios() {
        return ejecutar(() -> funcionarios.listar());
    }

    @Override
    public Funcionario buscarFuncionario(String id) {
        if (esVacio(id)) {
            return null;
        }
        return ejecutar(() -> funcionarios.buscarPorId(id.trim()));
    }

    @Override
    public List<Funcionario> buscarFuncionariosPorNombre(String nombre) {
        String texto = nombre == null ? "" : nombre.trim().toLowerCase();
        List<Funcionario> resultado = new ArrayList<>();
        for (Funcionario f : listarFuncionarios()) {
            if (f.getNombre().toLowerCase().contains(texto)) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    @Override
    public synchronized void agregarFuncionario(Funcionario f) {
        validarFuncionario(f);
        if (buscarFuncionario(f.getId()) != null) {
            throw new ServiceException("Ya existe un funcionario con el ID " + f.getId() + ".");
        }
        ejecutar(() -> {
            funcionarios.insertar(f);
            return null;
        });
    }

    @Override
    public synchronized void actualizarFuncionario(Funcionario f) {
        validarFuncionario(f);
        if (buscarFuncionario(f.getId()) == null) {
            throw new ServiceException("No existe el funcionario " + f.getId() + ".");
        }
        ejecutar(() -> {
            funcionarios.modificar(f);
            return null;
        });
    }

    @Override
    public synchronized void borrarFuncionario(String id) {
        ejecutar(() -> {
            funcionarios.eliminar(id);
            return null;
        });
    }

    @Override
    public List<CategoriaRecurso> listarCategorias() {
        return ejecutar(() -> categorias.listar());
    }

    @Override
    public CategoriaRecurso buscarCategoria(String id) {
        if (esVacio(id)) {
            return null;
        }
        for (CategoriaRecurso c : listarCategorias()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<CategoriaRecurso> buscarCategoriasPorDescripcion(String desc) {
        return ejecutar(() -> categorias.buscarPorDescripcion(desc == null ? "" : desc.trim()));
    }

    @Override
    public synchronized CategoriaRecurso agregarCategoria(String desc) {
        if (esVacio(desc)) {
            throw new ServiceException("La descripción es obligatoria.");
        }
        String texto = desc.trim();
        ejecutar(() -> {
            categorias.insertar(texto);
            return null;
        });

        CategoriaRecurso creada = null;
        for (CategoriaRecurso c : buscarCategoriasPorDescripcion(texto)) {
            if (c.getDesc().equalsIgnoreCase(texto)) {
                creada = c;
            }
        }
        return creada;
    }

    @Override
    public synchronized void actualizarCategoria(CategoriaRecurso c) {
        if (c == null || esVacio(c.getDesc())) {
            throw new ServiceException("La descripción es obligatoria.");
        }
        if (buscarCategoria(c.getId()) == null) {
            throw new ServiceException("No existe la categoría " + c.getId() + ".");
        }
        c.setDesc(c.getDesc().trim());
        ejecutar(() -> {
            categorias.modificar(c);
            return null;
        });
    }

    @Override
    public synchronized void borrarCategoria(String id) {
        ejecutar(() -> {
            categorias.eliminar(id);
            return null;
        });
    }

    @Override
    public List<Recurso> listarRecursos() {
        return ejecutar(() -> recursos.listar());
    }

    @Override
    public Recurso buscarRecurso(String id) {
        if (esVacio(id)) {
            return null;
        }
        return ejecutar(() -> recursos.buscarPorId(id.trim()));
    }

    @Override
    public List<Recurso> buscarRecursos(String idCategoria, String descripcion) {
        List<Recurso> base;
        if (esVacio(idCategoria)) {
            base = listarRecursos();
        } else {
            base = ejecutar(() -> recursos.listarPorCategoria(idCategoria));
        }

        String texto = descripcion == null ? "" : descripcion.trim().toLowerCase();
        List<Recurso> resultado = new ArrayList<>();
        for (Recurso r : base) {
            if (r.getDescripcion().toLowerCase().contains(texto)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public synchronized void agregarRecurso(Recurso r) {
        validarRecurso(r);
        if (buscarRecurso(r.getId()) != null) {
            throw new ServiceException("Ya existe un recurso con el ID " + r.getId() + ".");
        }
        ejecutar(() -> {
            recursos.insertar(r);
            return null;
        });
    }

    @Override
    public synchronized void actualizarRecurso(Recurso r) {
        validarRecurso(r);
        if (buscarRecurso(r.getId()) == null) {
            throw new ServiceException("No existe el recurso " + r.getId() + ".");
        }
        ejecutar(() -> {
            recursos.modificar(r);
            return null;
        });
    }

    @Override
    public synchronized void borrarRecurso(String id) {
        ejecutar(() -> {
            recursos.eliminar(id);
            return null;
        });
    }

    @Override
    public List<Reserva> listarReservas() {
        return ejecutar(() -> reservas.listar());
    }

    @Override
    public List<Reserva> listarReservasPorFuncionario(String idFuncionario) {
        return ejecutar(() -> reservas.listarPorFuncionario(idFuncionario));
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
        if (esVacio(idFuncionario) || buscarFuncionario(idFuncionario) == null) {
            throw new ServiceException("El funcionario no existe.");
        }

        List<String> idsRecursos = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (String idCategoria : idsCategorias) {
            CategoriaRecurso categoria = buscarCategoria(idCategoria);
            if (categoria == null) {
                throw new ServiceException("No existe la categoría " + idCategoria + ".");
            }
            List<Recurso> disponibles = ejecutar(() -> recursos.disponibles(idCategoria, fecha, inicio, fin));
            if (disponibles.isEmpty()) {
                noDisponibles.add(categoria.getDesc());
            } else {
                idsRecursos.add(disponibles.get(0).getId());
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new ServiceException("No hay recursos disponibles para: " + String.join(", ", noDisponibles));
        }

        Collections.sort(idsRecursos);

        String actividadLimpia = actividad.trim();
        return ejecutar(() -> {
            String idReserva = reservas.insertar(idFuncionario, actividadLimpia, actividadLimpia,
                    fecha, inicio, fin, idsRecursos);
            return reservas.buscarPorId(idReserva);
        });
    }

    @Override
    public synchronized void cancelarReserva(String idReserva, String idFuncionario) {
        ejecutar(() -> {
            reservas.cancelar(idReserva, idFuncionario);
            return null;
        });
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
        if (r.getCategoria() == null || buscarCategoria(r.getCategoria().getId()) == null) {
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
