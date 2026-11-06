package servidor;

import datos.Conexion;
import logica.ServiceMySQL;
import model.Funcionario;
import model.Usuario;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceMySQLIT {

    private static final ServiceMySQL service = new ServiceMySQL();

    @BeforeAll
    static void conectar() {
        Conexion.configurar(
                System.getProperty("bd.url", "jdbc:mysql://localhost:3306/reservas"),
                System.getProperty("bd.usuario", "root"),
                System.getProperty("bd.clave", ""));
        try (Connection con = Conexion.obtener()) {
            Assumptions.assumeTrue(con.isValid(2));
        } catch (SQLException e) {
            Assumptions.assumeTrue(false, "No hay base de datos MySQL disponible");
        }
    }

    @Test
    void loginDelAdministradorDemo() {
        Usuario usuario = service.login("admin", "1234");
        assertNotNull(usuario);
        assertEquals(Usuario.Rol.ADMINISTRADOR, usuario.getRol());
        assertNull(service.login("admin", "incorrecta"));
    }

    @Test
    void agregarBuscarYBorrarFuncionario() {
        service.agregarFuncionario(new Funcionario("IT-TEMP", "Usuario de Prueba", "1111-1111"));
        try {
            Funcionario encontrado = service.buscarFuncionario("IT-TEMP");
            assertNotNull(encontrado);
            assertEquals("Usuario de Prueba", encontrado.getNombre());
            assertThrows(red.ServiceException.class,
                    () -> service.agregarFuncionario(new Funcionario("IT-TEMP", "Repetido", "2222-2222")));
        } finally {
            service.borrarFuncionario("IT-TEMP");
        }
        assertNull(service.buscarFuncionario("IT-TEMP"));
    }
}
