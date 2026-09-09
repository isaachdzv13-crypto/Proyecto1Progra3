package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    private Reserva crearReservaValida() {
        return new Reserva(
                "1234",
                "R-001",
                "Reunion de equipo",
                "Reunion",
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalDate.now().plusDays(1)
        );
    }

    @Test
    void creaReservaValidaConEstadoActivaPorDefecto() {
        Reserva r = crearReservaValida();

        assertEquals(Reserva.Estado.ACTIVA, r.getEstado());
        assertEquals("R-001", r.getIdReserva());
        assertEquals("1234", r.getIdFuncionario());
    }

    @Test
    void rechazaHoraFinAntesQueHoraInicio() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Reserva(
                        "1234", "R-002", "Reunion", "Reunion",
                        LocalTime.of(10, 0),
                        LocalTime.of(9, 0), // fin antes que inicio
                        LocalDate.now().plusDays(1)
                )
        );
        assertTrue(ex.getMessage().contains("posterior"));
    }

    @Test
    void rechazaHoraFinIgualAHoraInicio() {
        assertThrows(IllegalArgumentException.class, () ->
                new Reserva(
                        "1234", "R-003", "Reunion", "Reunion",
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 0),
                        LocalDate.now().plusDays(1)
                )
        );
    }

    @Test
    void rechazaDescripcionVacia() {
        assertThrows(IllegalArgumentException.class, () ->
                new Reserva(
                        "1234", "R-004", "   ", "Reunion",
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        LocalDate.now().plusDays(1)
                )
        );
    }

    @Test
    void rechazaFechaNula() {
        assertThrows(IllegalArgumentException.class, () ->
                new Reserva(
                        "1234", "R-005", "Reunion", "Reunion",
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        null
                )
        );
    }
}
