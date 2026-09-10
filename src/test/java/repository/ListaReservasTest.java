package repository;

import model.Reserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ListaReservasTest {

    @Test
    void buscarReservaExistente() {
        ListaReservas lista = new ListaReservas();

        Reserva reserva = new Reserva(
                "1234",
                "RES-001",
                "Reunión de trabajo",
                "Reunión",
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalDate.now().plusDays(1)
        );

        lista.add(reserva);

        Reserva encontrada = lista.buscarPorId("RES-001");

        assertNotNull(encontrada);
        assertEquals("RES-001", encontrada.getIdReserva());
        assertEquals("1234", encontrada.getIdFuncionario());
    }

    @Test
    void buscarReservaInexistenteRetornaNull() {
        ListaReservas lista = new ListaReservas();

        Reserva encontrada = lista.buscarPorId("NO-EXISTE");

        assertNull(encontrada);
    }

    @Test
    void detectaReservaActivaConUnRecurso() {
        ListaReservas lista = new ListaReservas();
        model.CategoriaRecurso categoria = new model.CategoriaRecurso("Sala");
        model.Recurso recurso = new model.Recurso("REC-900", categoria, "Proyector");

        Reserva reserva = new Reserva("1234", "RES-900", "Reunion", "Reunion",
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalDate.now().plusDays(1));
        reserva.setRecursos(java.util.List.of(recurso));
        lista.add(reserva);

        assertTrue(lista.tieneReservaActivaConRecurso(recurso));
    }

    @Test
    void noDetectaReservaConRecursoDeUnaReservaCancelada() {
        ListaReservas lista = new ListaReservas();
        model.CategoriaRecurso categoria = new model.CategoriaRecurso("Sala");
        model.Recurso recurso = new model.Recurso("REC-901", categoria, "Proyector");

        Reserva reserva = new Reserva("1234", "RES-901", "Reunion", "Reunion",
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalDate.now().plusDays(1));
        reserva.setRecursos(java.util.List.of(recurso));
        reserva.setEstado(Reserva.Estado.CANCELADA);
        lista.add(reserva);

        assertFalse(lista.tieneReservaActivaConRecurso(recurso));
    }

    @Test
    void detectaReservaActivaDeUnFuncionario() {
        ListaReservas lista = new ListaReservas();
        Reserva reserva = new Reserva("5678", "RES-902", "Reunion", "Reunion",
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalDate.now().plusDays(1));
        lista.add(reserva);

        assertTrue(lista.tieneReservaActivaDeFuncionario("5678"));
        assertFalse(lista.tieneReservaActivaDeFuncionario("0000"));
    }
}
