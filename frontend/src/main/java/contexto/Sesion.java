package contexto;

import proxy.ServiceProxy;
import red.Notificacion;
import service.Service;

import java.io.IOException;
import java.util.function.Consumer;

public class Sesion {

    private static Service service;
    private static ServiceProxy proxy;

    public static void conectar(String host, int puerto) throws IOException {
        proxy = new ServiceProxy(host, puerto);
        service = proxy;
    }

    public static Service getService() {
        return service;
    }

    public static void setService(Service nuevo) {
        service = nuevo;
    }

    public static void setOyenteNotificaciones(Consumer<Notificacion> oyente) {
        if (proxy != null) {
            proxy.setOyente(oyente);
        }
    }
}
