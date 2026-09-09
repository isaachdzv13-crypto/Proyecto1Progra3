package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void funcionarioTieneClaveInicialIgualAlId() {
        Funcionario funcionario =
                new Funcionario("1234", "Ana Pérez", "8888-1111");

        assertEquals("1234", funcionario.getId());
        assertEquals("1234", funcionario.getClave());
        assertEquals(Usuario.Rol.FUNCIONARIO, funcionario.getRol());
    }

    @Test
    void permiteCambiarLaClave() {
        Funcionario funcionario =
                new Funcionario("1234", "Ana Pérez", "8888-1111");

        funcionario.setClave("claveNueva");

        assertEquals("claveNueva", funcionario.getClave());
    }

    @Test
    void rechazaIdVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Funcionario("", "Ana Pérez", "8888-1111");
        });
    }

    @Test
    void administradorTieneRolCorrecto() {
        Administrador administrador = new Administrador("admin", "1234");

        assertEquals(Usuario.Rol.ADMINISTRADOR, administrador.getRol());
        assertEquals("admin", administrador.getId());
        assertEquals("1234", administrador.getClave());
    }
}
