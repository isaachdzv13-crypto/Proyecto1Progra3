package repository;

import model.Reserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertSame;

public class ReservaRepositorioIT {
    @Test
    void guardaYBuscaUnaReserva() {
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

        assertSame(reserva, lista.buscarPorId("RES-001"));
    }
}
