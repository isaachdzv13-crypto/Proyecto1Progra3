package accesoDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Servicio {

    protected Connection conexion;

    protected void conectar()
            throws ClassNotFoundException, SQLException {

        Class.forName("oracle.jdbc.driver.OracleDriver");

        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String usuario = "system";
        String clave = System.getenv("ORACLE_PASSWORD");

        conexion = DriverManager.getConnection(url, usuario, clave);
    }

    protected void desconectar() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }
}
