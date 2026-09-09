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
}
