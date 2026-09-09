package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    @Test
    void creaReservaCorrectamente() {
        Reserva reserva = new Reserva(
                "1234",
                "RES-001",
                "Reunión de trabajo",
                "Reunión",
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                LocalDate.of(2026, 9, 15)
        );

        assertEquals("1234", reserva.getIdFuncionario());
        assertEquals("RES-001", reserva.getIdReserva());
        assertEquals("Reunión de trabajo", reserva.getDescripcion());
        assertEquals("Reunión", reserva.getActividad());
        assertEquals(LocalTime.of(8, 0), reserva.getHoraInicio());
        assertEquals(LocalTime.of(10, 0), reserva.getHoraFin());
        assertEquals(LocalDate.of(2026, 9, 15), reserva.getFecha());
        assertEquals(Reserva.Estado.ACTIVA, reserva.getEstado());
    }

    @Test
    void rechazaHoraFinalAnteriorAlInicio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Reserva(
                    "1234",
                    "RES-002",
                    "Reunión de trabajo",
                    "Reunión",
                    LocalTime.of(10, 0),
                    LocalTime.of(8, 0),
                    LocalDate.of(2026, 9, 15)
            );
        });
    }

    @Test
    void rechazaHoraFinalIgualAlInicio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Reserva(
                    "1234",
                    "RES-003",
                    "Reunión de trabajo",
                    "Reunión",
                    LocalTime.of(8, 0),
                    LocalTime.of(8, 0),
                    LocalDate.of(2026, 9, 15)
            );
        });
    }

    @Test
    void rechazaDescripcionVacia() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Reserva(
                    "1234",
                    "RES-004",
                    "",
                    "Reunión",
                    LocalTime.of(8, 0),
                    LocalTime.of(10, 0),
                    LocalDate.of(2026, 9, 15)
            );
        });
    }

    @Test
    void asignaRecursosALaReserva() {
        CategoriaRecurso categoria =
                new CategoriaRecurso("CAT-000001", "Proyectores");

        Recurso recurso =
                new Recurso("REC-001", categoria, "Proyector Epson");

        Reserva reserva = new Reserva(
                "1234",
                "RES-005",
                "Presentación del proyecto",
                "Exposición",
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                LocalDate.of(2026, 9, 15)
        );

        reserva.setRecursos(List.of(recurso));

        assertEquals(1, reserva.getRecursos().size());
        assertEquals("REC-001", reserva.getRecursos().get(0).getId());
    }

    @Test
    void permiteCancelarLaReserva() {
        Reserva reserva = new Reserva(
                "1234",
                "RES-006",
                "Reunión de trabajo",
                "Reunión",
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                LocalDate.of(2026, 9, 15)
        );

        reserva.setEstado(Reserva.Estado.CANCELADA);

        assertEquals(Reserva.Estado.CANCELADA, reserva.getEstado());
    }
}