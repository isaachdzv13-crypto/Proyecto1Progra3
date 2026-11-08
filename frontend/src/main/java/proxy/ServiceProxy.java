package proxy;

import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;
import model.Usuario;
import red.Notificacion;
import red.Peticion;
import red.Respuesta;
import red.ServiceException;
import service.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ServiceProxy implements Service {

    private final Socket socket;
    private final ObjectOutputStream salida;
    private final ObjectInputStream entrada;
    private final BlockingQueue<Respuesta> respuestas = new LinkedBlockingQueue<>();
    private volatile Consumer<Notificacion> oyente;
    private volatile boolean conectado;

    public ServiceProxy(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();
        entrada = new ObjectInputStream(socket.getInputStream());
        conectado = true;

        Thread hilo = new Thread(this::escuchar, "proxy-escucha");
        hilo.setDaemon(true);
        hilo.start();
    }

    public void setOyente(Consumer<Notificacion> oyente) {
        this.oyente = oyente;
    }

    public void cerrar() {
        conectado = false;
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }

    private void escuchar() {
        try {
            while (conectado) {
                Object mensaje = entrada.readObject();
                if (mensaje instanceof Respuesta) {
                    respuestas.put((Respuesta) mensaje);
                } else if (mensaje instanceof Notificacion) {
                    Consumer<Notificacion> actual = oyente;
                    if (actual != null) {
                        actual.accept((Notificacion) mensaje);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            if (conectado) {
                conectado = false;
                respuestas.offer(Respuesta.error("Se perdió la conexión con el servidor."));
            }
        }
    }

    private synchronized Object enviar(String operacion, Object... parametros) {
        if (!conectado) {
            throw new ServiceException("No hay conexión con el servidor.");
        }
        try {
            respuestas.clear();
            salida.writeObject(new Peticion(operacion, parametros));
            salida.flush();
            salida.reset();

            Respuesta respuesta = respuestas.poll(30, TimeUnit.SECONDS);
            if (respuesta == null) {
                throw new ServiceException("El servidor no respondió.");
            }
            if (!respuesta.isExito()) {
                throw new ServiceException(respuesta.getError());
            }
            return respuesta.getDatos();
        } catch (IOException e) {
            conectado = false;
            throw new ServiceException("Se perdió la conexión con el servidor.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceException("Se interrumpió la comunicación con el servidor.", e);
        }
    }

    @Override
    public Usuario login(String id, String clave) {
        return (Usuario) enviar("login", id, clave);
    }

    @Override
    public void cerrarSesion() {
        enviar("cerrarSesion");
    }

    @Override
    public void cambiarClave(String id, String claveActual, String claveNueva) {
        enviar("cambiarClave", id, claveActual, claveNueva);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Funcionario> listarFuncionarios() {
        return (List<Funcionario>) enviar("listarFuncionarios");
    }

    @Override
    public Funcionario buscarFuncionario(String id) {
        return (Funcionario) enviar("buscarFuncionario", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Funcionario> buscarFuncionariosPorNombre(String nombre) {
        return (List<Funcionario>) enviar("buscarFuncionariosPorNombre", nombre);
    }

    @Override
    public void agregarFuncionario(Funcionario funcionario) {
        enviar("agregarFuncionario", funcionario);
    }

    @Override
    public void actualizarFuncionario(Funcionario funcionario) {
        enviar("actualizarFuncionario", funcionario);
    }

    @Override
    public void borrarFuncionario(String id) {
        enviar("borrarFuncionario", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CategoriaRecurso> listarCategorias() {
        return (List<CategoriaRecurso>) enviar("listarCategorias");
    }

    @Override
    public CategoriaRecurso buscarCategoria(String id) {
        return (CategoriaRecurso) enviar("buscarCategoria", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CategoriaRecurso> buscarCategoriasPorDescripcion(String desc) {
        return (List<CategoriaRecurso>) enviar("buscarCategoriasPorDescripcion", desc);
    }

    @Override
    public CategoriaRecurso agregarCategoria(String desc) {
        return (CategoriaRecurso) enviar("agregarCategoria", desc);
    }

    @Override
    public void actualizarCategoria(CategoriaRecurso categoria) {
        enviar("actualizarCategoria", categoria);
    }

    @Override
    public void borrarCategoria(String id) {
        enviar("borrarCategoria", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Recurso> listarRecursos() {
        return (List<Recurso>) enviar("listarRecursos");
    }

    @Override
    public Recurso buscarRecurso(String id) {
        return (Recurso) enviar("buscarRecurso", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Recurso> buscarRecursos(String idCategoria, String descripcion) {
        return (List<Recurso>) enviar("buscarRecursos", idCategoria, descripcion);
    }

    @Override
    public void agregarRecurso(Recurso recurso) {
        enviar("agregarRecurso", recurso);
    }

    @Override
    public void actualizarRecurso(Recurso recurso) {
        enviar("actualizarRecurso", recurso);
    }

    @Override
    public void borrarRecurso(String id) {
        enviar("borrarRecurso", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reserva> listarReservas() {
        return (List<Reserva>) enviar("listarReservas");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reserva> listarReservasPorFuncionario(String idFuncionario) {
        return (List<Reserva>) enviar("listarReservasPorFuncionario", idFuncionario);
    }

    @Override
    public Reserva crearReserva(String idFuncionario, String actividad, LocalDate fecha,
                                LocalTime inicio, LocalTime fin, List<String> idsCategorias) {
        return (Reserva) enviar("crearReserva", idFuncionario, actividad, fecha, inicio, fin,
                new ArrayList<>(idsCategorias));
    }

    @Override
    public void cancelarReserva(String idReserva, String idFuncionario) {
        enviar("cancelarReserva", idReserva, idFuncionario);
    }
}
