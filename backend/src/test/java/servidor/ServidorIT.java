package servidor;

import model.Funcionario;
import model.Reserva;
import model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import red.Notificacion;
import red.Peticion;
import red.Respuesta;
import red.ServiceException;
import service.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServidorIT {

    private Servidor servidor;

    private static class Cliente {
        final Socket socket;
        final ObjectOutputStream salida;
        final ObjectInputStream entrada;

        Cliente(int puerto) throws IOException {
            socket = new Socket("localhost", puerto);
            socket.setSoTimeout(3000);
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            entrada = new ObjectInputStream(socket.getInputStream());
        }

        Respuesta pedir(String operacion, Object... parametros) throws Exception {
            salida.writeObject(new Peticion(operacion, parametros));
            salida.flush();
            return (Respuesta) entrada.readObject();
        }

        void cerrar() throws IOException {
            socket.close();
        }
    }

    private Service crearServiceFalso() {
        return (Service) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Service.class},
                (proxy, metodo, args) -> {
                    switch (metodo.getName()) {
                        case "login":
                            if ("1234".equals(args[1])) {
                                return new Funcionario((String) args[0], "Funcionario " + args[0], "8888-8888");
                            }
                            return null;
                        case "buscarFuncionario":
                            return new Funcionario((String) args[0], "Funcionario " + args[0], "8888-8888");
                        case "crearReserva":
                            if ("Choque".equals(args[1])) {
                                throw new ServiceException("No hay recursos disponibles");
                            }
                            return new Reserva((String) args[0], "RES-000001", (String) args[1], (String) args[1],
                                    (LocalTime) args[3], (LocalTime) args[4], (LocalDate) args[2]);
                        case "listarCategorias":
                            return List.of();
                        default:
                            return null;
                    }
                });
    }

    @BeforeEach
    void iniciar() throws IOException {
        servidor = new Servidor(0, crearServiceFalso());
        servidor.iniciar();
    }

    @AfterEach
    void detener() {
        servidor.detener();
    }

    @Test
    void loginCorrectoDevuelveElUsuario() throws Exception {
        Cliente cliente = new Cliente(servidor.getPuerto());
        Respuesta respuesta = cliente.pedir("login", "1234", "1234");
        assertTrue(respuesta.isExito());
        assertEquals("1234", ((Usuario) respuesta.getDatos()).getId());
        cliente.cerrar();
    }

    @Test
    void loginIncorrectoDevuelveNull() throws Exception {
        Cliente cliente = new Cliente(servidor.getPuerto());
        Respuesta respuesta = cliente.pedir("login", "1234", "mala");
        assertTrue(respuesta.isExito());
        assertNull(respuesta.getDatos());
        cliente.cerrar();
    }

    @Test
    void operacionDesconocidaDevuelveError() throws Exception {
        Cliente cliente = new Cliente(servidor.getPuerto());
        Respuesta respuesta = cliente.pedir("noExiste");
        assertFalse(respuesta.isExito());
        cliente.cerrar();
    }

    @Test
    void errorDeLogicaLlegaAlClienteYLaConexionSigueViva() throws Exception {
        Cliente cliente = new Cliente(servidor.getPuerto());
        cliente.pedir("login", "1234", "1234");

        Respuesta error = cliente.pedir("crearReserva", "1234", "Choque", LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(9, 0), new java.util.ArrayList<>(List.of("CAT-000001")));
        assertFalse(error.isExito());
        assertEquals("No hay recursos disponibles", error.getError());

        Respuesta siguiente = cliente.pedir("listarCategorias");
        assertTrue(siguiente.isExito());
        cliente.cerrar();
    }

    @Test
    void laNuevaReservaSeNotificaALosOtrosClientesConSesion() throws Exception {
        Cliente emisor = new Cliente(servidor.getPuerto());
        Cliente receptor = new Cliente(servidor.getPuerto());
        Cliente sinSesion = new Cliente(servidor.getPuerto());

        emisor.pedir("login", "1234", "1234");
        receptor.pedir("login", "5678", "1234");
        sinSesion.socket.setSoTimeout(700);
        emisor.socket.setSoTimeout(700);

        Respuesta respuesta = emisor.pedir("crearReserva", "1234", "Reunion", LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(9, 0), new java.util.ArrayList<>(List.of("CAT-000001")));
        assertTrue(respuesta.isExito());

        Notificacion notificacion = (Notificacion) receptor.entrada.readObject();
        assertEquals("Reunion", notificacion.getReserva().getActividad());
        assertEquals("Funcionario 1234", notificacion.getNombreFuncionario());

        assertThrows(SocketTimeoutException.class, () -> emisor.entrada.readObject());
        assertThrows(SocketTimeoutException.class, () -> sinSesion.entrada.readObject());

        emisor.cerrar();
        receptor.cerrar();
        sinSesion.cerrar();
    }

    @Test
    void variosClientesSonAtendidosAlMismoTiempo() throws Exception {
        Cliente a = new Cliente(servidor.getPuerto());
        Cliente b = new Cliente(servidor.getPuerto());
        assertTrue(a.pedir("listarCategorias").isExito());
        assertTrue(b.pedir("listarCategorias").isExito());
        assertTrue(a.pedir("listarCategorias").isExito());
        a.cerrar();
        b.cerrar();
    }
}
