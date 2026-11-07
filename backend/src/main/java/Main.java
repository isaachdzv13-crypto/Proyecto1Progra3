import logica.ServiceReal;
import servidor.Servidor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties config = cargarConfiguracion();

        int puerto = Integer.parseInt(config.getProperty("puerto", "5000"));
        if (args.length > 0) {
            puerto = Integer.parseInt(args[0]);
        }

        try {
            Servidor servidor = new Servidor(puerto, new ServiceReal());
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
            System.out.println("No se encontro backend.properties, se usa el puerto por defecto.");
        }
        return config;
    }
}
