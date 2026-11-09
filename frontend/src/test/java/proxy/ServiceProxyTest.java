package proxy;

import model.Funcionario;
import model.Reserva;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import red.Notificacion;
import red.Peticion;
import red.Respuesta;
import red.ServiceException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ServiceProxyTest {

    private ServerSocket servidorFalso;
    private ServiceProxy proxy;

    @BeforeEach
    void iniciar() throws Exception {
        servidorFalso = new ServerSocket(0);
        Thread hilo = new Thread(() -> atender());
        hilo.setDaemon(true);
        hilo.start();
        proxy = new ServiceProxy("localhost", servidorFalso.getLocalPort());
    }

    @AfterEach
    void detener() throws Exception {
        proxy.cerrar();
        servidorFalso.close();
    }

    private void atender() {
        try (Socket socket = servidorFalso.accept()) {
            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Peticion peticion = (Peticion) entrada.readObject();
                Object[] p = peticion.getParametros();
                switch (peticion.getOperacion()) {
                    case "buscarFuncionario":
                        salida.writeObject(Respuesta.ok(new Funcionario((String) p[0], "Ana", "8888-8888")));
                        break;
                    case "borrarFuncionario":
                        salida.writeObject(Respuesta.error("No se puede borrar"));
                        break;
                    case "cerrarSesion":
                        Reserva reserva = new Reserva("1234", "RES-000001", "Reunion", "Reunion",
                                LocalTime.of(8, 0), LocalTime.of(9, 0), LocalDate.now().plusDays(1));
                        salida.writeObject(new Notificacion(reserva, "Ana"));
                        salida.writeObject(Respuesta.ok(null));
                        break;
                    default:
                        salida.writeObject(Respuesta.ok(null));
                }
                salida.flush();
                salida.reset();
            }
        } catch (Exception e) {

        }
    }

    @Test
    void elProxyEnviaLaPeticionYDevuelveLaRespuesta() {
        Funcionario f = proxy.buscarFuncionario("1234");
        assertEquals("1234", f.getId());
        assertEquals("Ana", f.getNombre());
    }

    @Test
    void unaRespuestaConErrorSeConvierteEnServiceException() {
        ServiceException e = assertThrows(ServiceException.class, () -> proxy.borrarFuncionario("1234"));
        assertEquals("No se puede borrar", e.getMessage());
    }

    @Test
    void lasNotificacionesLlegaranAlOyenteSinMezclarseConLasRespuestas() throws Exception {
        BlockingQueue<Notificacion> recibidas = new LinkedBlockingQueue<>();
        proxy.setOyente(recibidas::add);

        proxy.cerrarSesion();

        Notificacion n = recibidas.poll(2, TimeUnit.SECONDS);
        assertNotNull(n);
        assertEquals("Ana", n.getNombreFuncionario());
        assertEquals("Funcionario", proxy.buscarFuncionario("1").getNombre().isEmpty() ? "" : "Funcionario");
    }
}
