package repository;

import model.CategoriaRecurso;
import model.Recurso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class ListaRecursosTest {

    private ListaRecursos recursos;
    private CategoriaRecurso salas;
    private CategoriaRecurso computadoras;

    @BeforeEach
    void prepararLista() {
        recursos = new ListaRecursos();

        salas = new CategoriaRecurso("CAT-000001", "Salas");
        computadoras =
                new CategoriaRecurso("CAT-000002", "Computadoras");

        recursos.addRecurso(
                new Recurso("REC-1", salas, "Sala primer piso")
        );
        recursos.addRecurso(
                new Recurso("REC-2", computadoras, "Laptop Dell")
        );
    }
    @Test
    void buscarRecursoPorId(){
        Recurso recurso= recursos.buscarPorId("REC-1");
        assertNotNull(recurso);
        assertEquals("Sala primer piso",recurso.getDescripcion());

    }
    @Test
    void borraRecurso() {
        boolean eliminado = recursos.borrarRecurso("REC-1");

        assertTrue(eliminado);
        assertNull(recursos.buscarPorId("REC-1"));
    }


}
