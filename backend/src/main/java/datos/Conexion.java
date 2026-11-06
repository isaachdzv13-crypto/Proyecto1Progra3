package datos;

import red.ServiceException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static String url = "jdbc:mysql://localhost:3306/reservas";
    private static String usuario = "root";
    private static String clave = "";

    public static void configurar(String nuevaUrl, String nuevoUsuario, String nuevaClave) {
        url = nuevaUrl;
        usuario = nuevoUsuario;
        clave = nuevaClave;
    }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    public static ServiceException error(SQLException e) {
        return new ServiceException("Error en la base de datos: " + e.getMessage(), e);
    }
}
