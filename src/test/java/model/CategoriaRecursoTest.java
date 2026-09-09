package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaRecursoTest {

    @Test
    void generaIdConElFormatoEsperado() {
        CategoriaRecurso c = new CategoriaRecurso("Sala de reuniones");

        assertTrue(c.getId().startsWith("CAT-"),
                "El id generado deberia empezar con 'CAT-', pero fue: " + c.getId());
        assertEquals("Sala de reuniones", c.getDesc());
    }

    @Test
    void dosCategoriasNuevasTienenIdsDiferentes() {
        CategoriaRecurso c1 = new CategoriaRecurso("Categoria A");
        CategoriaRecurso c2 = new CategoriaRecurso("Categoria B");

        assertNotEquals(c1.getId(), c2.getId());
    }

    @Test
    void constructorConIdExplicitoLoRespeta() {
        CategoriaRecurso c = new CategoriaRecurso("CAT-000099", "Categoria cargada de XML");

        assertEquals("CAT-000099", c.getId());
        assertEquals("Categoria cargada de XML", c.getDesc());
    }
}
