package repository;

import model.CategoriaRecurso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaRecursoTest {

    @Test
    void creaCategoriaConDescripcion() {
        CategoriaRecurso categoria = new CategoriaRecurso("Sala de reuniones");

        assertEquals("Sala de reuniones", categoria.getDesc());
        assertTrue(categoria.getId().startsWith("CAT-"));
    }

    @Test
    void generaIdsDiferentes() {
        CategoriaRecurso primera = new CategoriaRecurso("Sala");
        CategoriaRecurso segunda = new CategoriaRecurso("Laptop");

        assertNotEquals(primera.getId(), segunda.getId());
    }

    @Test
    void conservaIdCargadoDesdeXml() {
        CategoriaRecurso categoria =
                new CategoriaRecurso("CAT-000050", "Proyectores");

        assertEquals("CAT-000050", categoria.getId());
        assertEquals("Proyectores", categoria.getDesc());
    }
}
