package servidor;

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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteHandler extends Thread {

    private final Socket socket;
    private final Servidor servidor;
    private final Service service;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private volatile Usuario usuarioActual;

    public ClienteHandler(Socket socket, Servidor servidor, Service service) {
        this.socket = socket;
        this.servidor = servidor;
        this.service = service;
    }

    public boolean tieneSesion() {
        return usuarioActual != null;
    }

    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            entrada = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                Peticion peticion = (Peticion) entrada.readObject();
                enviar(procesar(peticion));
            }
        } catch (EOFException e) {
            System.out.println("El cliente cerró la conexión.");
        } catch (IOException | ClassNotFoundException e) {
            if (!socket.isClosed()) {
                System.err.println("Error con el cliente: " + e.getMessage());
            }
        } finally {
            cerrar();
            servidor.quitar(this);
        }
    }

    public void enviar(Object mensaje) {
        synchronized (this) {
            try {
                salida.writeObject(mensaje);
                salida.flush();
                salida.reset();
            } catch (IOException e) {
                System.err.println("No se pudo enviar al cliente: " + e.getMessage());
            }
        }
    }

    public void cerrar() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el socket: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Respuesta procesar(Peticion peticion) {
        Object[] p = peticion.getParametros();
        try {
            switch (peticion.getOperacion()) {
                case "login":
                    Usuario usuario = service.login((String) p[0], (String) p[1]);
                    usuarioActual = usuario;
                    return Respuesta.ok(usuario);
                case "cerrarSesion":
                    usuarioActual = null;
                    service.cerrarSesion();
                    return Respuesta.ok(null);
                case "cambiarClave":
                    service.cambiarClave((String) p[0], (String) p[1], (String) p[2]);
                    return Respuesta.ok(null);

                case "listarFuncionarios":
                    return Respuesta.ok(new ArrayList<>(service.listarFuncionarios()));
                case "buscarFuncionario":
                    return Respuesta.ok(service.buscarFuncionario((String) p[0]));
                case "buscarFuncionariosPorNombre":
                    return Respuesta.ok(new ArrayList<>(service.buscarFuncionariosPorNombre((String) p[0])));
                case "agregarFuncionario":
                    service.agregarFuncionario((Funcionario) p[0]);
                    return Respuesta.ok(null);
                case "actualizarFuncionario":
                    service.actualizarFuncionario((Funcionario) p[0]);
                    return Respuesta.ok(null);
                case "borrarFuncionario":
                    service.borrarFuncionario((String) p[0]);
                    return Respuesta.ok(null);

                case "listarCategorias":
                    return Respuesta.ok(new ArrayList<>(service.listarCategorias()));
                case "buscarCategoria":
                    return Respuesta.ok(service.buscarCategoria((String) p[0]));
                case "buscarCategoriasPorDescripcion":
                    return Respuesta.ok(new ArrayList<>(service.buscarCategoriasPorDescripcion((String) p[0])));
                case "agregarCategoria":
                    return Respuesta.ok(service.agregarCategoria((String) p[0]));
                case "actualizarCategoria":
                    service.actualizarCategoria((CategoriaRecurso) p[0]);
                    return Respuesta.ok(null);
                case "borrarCategoria":
                    service.borrarCategoria((String) p[0]);
                    return Respuesta.ok(null);

                case "listarRecursos":
                    return Respuesta.ok(new ArrayList<>(service.listarRecursos()));
                case "buscarRecurso":
                    return Respuesta.ok(service.buscarRecurso((String) p[0]));
                case "buscarRecursos":
                    return Respuesta.ok(new ArrayList<>(service.buscarRecursos((String) p[0], (String) p[1])));
                case "agregarRecurso":
                    service.agregarRecurso((Recurso) p[0]);
                    return Respuesta.ok(null);
                case "actualizarRecurso":
                    service.actualizarRecurso((Recurso) p[0]);
                    return Respuesta.ok(null);
                case "borrarRecurso":
                    service.borrarRecurso((String) p[0]);
                    return Respuesta.ok(null);

                case "listarReservas":
                    return Respuesta.ok(new ArrayList<>(service.listarReservas()));
                case "listarReservasPorFuncionario":
                    return Respuesta.ok(new ArrayList<>(service.listarReservasPorFuncionario((String) p[0])));
                case "crearReserva":
                    return crearReserva(p);
                case "cancelarReserva":
                    service.cancelarReserva((String) p[0], (String) p[1]);
                    return Respuesta.ok(null);

                default:
                    return Respuesta.error("Operación desconocida: " + peticion.getOperacion());
            }
        } catch (ServiceException e) {
            return Respuesta.error(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Respuesta.error("Error interno del servidor: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Respuesta crearReserva(Object[] p) {
        String idFuncionario = (String) p[0];
        Reserva reserva = service.crearReserva(idFuncionario, (String) p[1], (LocalDate) p[2],
                (LocalTime) p[3], (LocalTime) p[4], (List<String>) p[5]);

        Funcionario funcionario = service.buscarFuncionario(idFuncionario);
        String nombre = funcionario != null ? funcionario.getNombre() : idFuncionario;
        servidor.notificarOtros(this, new Notificacion(reserva, nombre));

        return Respuesta.ok(reserva);
    }
}
