package servidor;

import red.Notificacion;
import service.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {

    private final int puerto;
    private final Service service;
    private final List<ClienteHandler> clientes = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;
    private volatile boolean activo;

    public Servidor(int puerto, Service service) {
        this.puerto = puerto;
        this.service = service;
    }

    public void iniciar() throws IOException {
        serverSocket = new ServerSocket(puerto);
        activo = true;
        Thread hilo = new Thread(this::aceptarClientes, "servidor-aceptar");
        hilo.start();
    }

    public int getPuerto() {
        return serverSocket.getLocalPort();
    }

    public void detener() {
        activo = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
        for (ClienteHandler cliente : clientes) {
            cliente.cerrar();
        }
    }

    private void aceptarClientes() {
        while (activo) {
            try {
                Socket socket = serverSocket.accept();
                ClienteHandler cliente = new ClienteHandler(socket, this, service);
                clientes.add(cliente);
                cliente.start();
                System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                if (activo) {
                    System.err.println("Error al aceptar cliente: " + e.getMessage());
                }
            }
        }
    }

    public void notificarOtros(ClienteHandler origen, Notificacion notificacion) {
        for (ClienteHandler cliente : clientes) {
            if (cliente != origen && cliente.tieneSesion()) {
                cliente.enviar(notificacion);
            }
        }
    }

    public void quitar(ClienteHandler cliente) {
        clientes.remove(cliente);
        System.out.println("Cliente desconectado");
    }
}
