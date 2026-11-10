package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Usuario es abstracta, asi que probamos sus validaciones a traves de
 * Funcionario, que es una de sus subclases concretas.
 */
class UsuarioTest {

    @Test
    void creaFuncionarioConDatosValidos() {
        Funcionario f = new Funcionario("1234", "Ana Perez", "8888-1111");

        assertEquals("1234", f.getId());
        assertEquals("Ana Perez", f.getNombre());
        assertEquals(Usuario.Rol.FUNCIONARIO, f.getRol());
        // La clave inicial de un funcionario nuevo es igual a su id
        assertEquals("1234", f.getClave());
    }

    @Test
    void rechazaIdVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Funcionario("   ", "Ana Perez", "8888-1111"));
    }

    @Test
    void rechazaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Funcionario(null, "Ana Perez", "8888-1111"));
    }

    @Test
    void permiteCambiarClaveDespuesDeCrearlo() {
        Funcionario f = new Funcionario("1234", "Ana Perez", "8888-1111");

        f.setClave("nuevaClave123");

        assertEquals("nuevaClave123", f.getClave());
    }
}
