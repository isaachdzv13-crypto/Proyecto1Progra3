package logica;

import model.Funcionario;
import org.junit.jupiter.api.Test;
import red.ServiceException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceRealTest {

    private final ServiceMySQL service = new ServiceMySQL();

    private ServiceException reservar(String actividad, LocalDate fecha, LocalTime inicio,
                                      LocalTime fin, List<String> categorias) {
        return assertThrows(ServiceException.class,
                () -> service.crearReserva("1234", actividad, fecha, inicio, fin, categorias));
    }

    @Test
    void loginConCamposVaciosDevuelveNull() {
        assertNull(service.login("", "1234"));
        assertNull(service.login("admin", null));
    }

    @Test
    void reservaSinActividadEsRechazada() {
        ServiceException e = reservar(" ", LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(9, 0), List.of("CAT-000001"));
        assertEquals("La actividad es obligatoria.", e.getMessage());
    }

    @Test
    void reservaConFechaPasadaEsRechazada() {
        ServiceException e = reservar("Reunion", LocalDate.now().minusDays(1),
                LocalTime.of(8, 0), LocalTime.of(9, 0), List.of("CAT-000001"));
        assertEquals("La fecha no puede ser anterior a hoy.", e.getMessage());
    }

    @Test
    void reservaConHoraFinalAnteriorEsRechazada() {
        ServiceException e = reservar("Reunion", LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(9, 0), List.of("CAT-000001"));
        assertEquals("La hora final debe ser posterior a la hora de inicio.", e.getMessage());
    }

    @Test
    void reservaSinCategoriasEsRechazada() {
        ServiceException e = reservar("Reunion", LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(9, 0), List.of());
        assertEquals("Seleccione al menos una categoria.", e.getMessage());
    }

    @Test
    void funcionarioSinNombreEsRechazado() {
        ServiceException e = assertThrows(ServiceException.class,
                () -> service.agregarFuncionario(new Funcionario("1234", " ", "8888-8888")));
        assertEquals("El nombre es obligatorio.", e.getMessage());
    }

    @Test
    void categoriaSinDescripcionEsRechazada() {
        assertThrows(ServiceException.class, () -> service.agregarCategoria(""));
    }

    @Test
    void cambiarClaveConCamposVaciosEsRechazado() {
        assertThrows(ServiceException.class, () -> service.cambiarClave("1234", "", "nueva"));
    }
}
