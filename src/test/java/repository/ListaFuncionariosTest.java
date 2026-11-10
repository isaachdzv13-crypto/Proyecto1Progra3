package repository;

import model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListaFuncionariosTest {

    private ListaFuncionarios lista;

    @BeforeEach
    void setUp() {
        lista = new ListaFuncionarios();
        lista.addRecurso(new Funcionario("1111", "Ana Perez", "8888-0001"));
        lista.addRecurso(new Funcionario("2222", "Beto Solis", "8888-0002"));
    }

    @Test
    void buscaFuncionarioExistentePorId() {
        Funcionario encontrado = lista.buscarPorId("1111");

        assertNotNull(encontrado);
        assertEquals("Ana Perez", encontrado.getNombre());
    }

    @Test
    void buscarPorIdInexistenteRetornaNulo() {
        assertNull(lista.buscarPorId("9999"));
    }

    @Test
    void buscaPorNombreParcialSinImportarMayusculas() {
        List<Funcionario> resultado = lista.buscarPorNombre("ana");

        assertEquals(1, resultado.size());
        assertEquals("1111", resultado.get(0).getId());
    }

    @Test
    void borrarEliminaAlFuncionarioDeLaLista() {
        lista.borrarRecurso("1111");

        assertNull(lista.buscarPorId("1111"));
        assertEquals(1, lista.listarTodos().size());
    }
}
