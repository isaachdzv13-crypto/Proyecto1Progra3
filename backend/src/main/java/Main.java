import datos.Conexion;
import logica.ServiceMySQL;
import logica.ServiceReal;
import servidor.Servidor;
import service.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties config = cargarConfiguracion();

        int puerto = Integer.parseInt(config.getProperty("puerto", "5000"));
        String url = config.getProperty("bd.url", "jdbc:mysql://localhost:3306/reservas");
        String usuario = config.getProperty("bd.usuario", "root");
        String clave = config.getProperty("bd.clave", "");

        if (args.length > 0) {
            puerto = Integer.parseInt(args[0]);
        }

        String capa = config.getProperty("capaDatos", "isaac");
        Service service;
        if (capa.equalsIgnoreCase("mysql")) {
            Conexion.configurar(url, usuario, clave);
            service = new ServiceMySQL();
        } else {
            service = new ServiceReal();
        }

        try {
            Servidor servidor = new Servidor(puerto, service);
            servidor.iniciar();
            System.out.println("Backend escuchando en el puerto " + puerto);
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }

    private static Properties cargarConfiguracion() {
        Properties config = new Properties();
        try (FileInputStream archivo = new FileInputStream("backend.properties")) {
            config.load(archivo);
        } catch (IOException e) {
            System.out.println("No se encontro backend.properties, se usan los valores por defecto.");
        }
        return config;
    }
}
