package repository;

import model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ListaFuncionariosTest {
    private ListaFuncionarios funcionarios;
    @BeforeEach
    void prepararLista() {
        funcionarios = new ListaFuncionarios();
        funcionarios.addRecurso(
                new Funcionario("1111", "Ana Pérez", "8888-1111")
        );
        funcionarios.addRecurso(
                new Funcionario("2222", "Carlos Rojas", "8888-2222")
        );
    }
    @Test
    void buscarFuncionarioPorId(){
        Funcionario encontrado= funcionarios.buscarPorId("1111");
        assertNotNull(encontrado);
        assertEquals("Ana Pérez",encontrado.getNombre());
    }
    @Test
    void retornaNullSiNoExiste() {
        Funcionario encontrado = funcionarios.buscarPorId("9999");

        assertNull(encontrado);
    }
    @Test
    void borraFuncionario() {
        funcionarios.borrarRecurso("1111");

        assertNull(funcionarios.buscarPorId("1111"));
        assertEquals(1, funcionarios.listarTodos().size());
    }

}
