package repository;

import model.Reserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ListaReservasTest {

    @Test
    void cancelarUnaReservaFuturaLaMarcaComoCancelada() {
        ListaReservas lista = new ListaReservas();
        Reserva futura = new Reserva("1234", "R-100", "Reunion", "Reunion",
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalDate.now().plusDays(2));
        lista.add(futura);

        String error = lista.cancelar("R-100");

        assertNull(error, "No deberia haber error al cancelar una reserva futura");
        assertEquals(Reserva.Estado.CANCELADA, futura.getEstado());
    }

    @Test
    void noSePuedeCancelarUnaReservaQueNoEsFutura() {
        ListaReservas lista = new ListaReservas();
        Reserva pasada = new Reserva("1234", "R-101", "Reunion", "Reunion",
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalDate.now());
        lista.add(pasada);

        String error = lista.cancelar("R-101");

        assertNotNull(error);
        assertEquals(Reserva.Estado.ACTIVA, pasada.getEstado());
    }

    @Test
    void cancelarUnaReservaInexistenteDaError() {
        ListaReservas lista = new ListaReservas();

        String error = lista.cancelar("NO-EXISTE");

        assertNotNull(error);
    }
}
