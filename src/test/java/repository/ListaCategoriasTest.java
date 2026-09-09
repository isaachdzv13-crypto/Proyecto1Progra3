package repository;

import model.CategoriaRecurso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListaCategoriasTest {

    private ListaCategorias categorias;

    @BeforeEach
    void prepararLista() {
        categorias = new ListaCategorias();
        categorias.addCategoria(
                new CategoriaRecurso("CAT-000001", "Sala de reuniones")
        );
        categorias.addCategoria(
                new CategoriaRecurso("CAT-000002", "Laptop Windows")
        );
    }

    @Test
    void buscaCategoriaPorId() {
        CategoriaRecurso categoria =
                categorias.buscarPorId("CAT-000001");

        assertNotNull(categoria);
        assertEquals("Sala de reuniones", categoria.getDesc());
    }

    @Test
    void buscaCategoriaPorDescripcion() {
        List<CategoriaRecurso> resultado =
                categorias.buscarPorDescripcion("laptop");

        assertEquals(1, resultado.size());
        assertEquals("CAT-000002", resultado.get(0).getId());
    }

    @Test
    void borraCategoria() {
        boolean eliminada =
                categorias.borrarCategoria("Sala de reuniones");

        assertTrue(eliminada);
        assertNull(categorias.buscarPorId("CAT-000001"));
    }

    @Test
    void noBorraCategoriaInexistente() {
        boolean eliminada =
                categorias.borrarCategoria("No existe");

        assertFalse(eliminada);
        assertEquals(2, categorias.listarTodas().size());
    }
}
