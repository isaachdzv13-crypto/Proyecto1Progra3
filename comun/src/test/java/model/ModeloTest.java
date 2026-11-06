package model;

import org.junit.jupiter.api.Test;
import red.Notificacion;
import red.Peticion;
import red.Respuesta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ModeloTest {

    private Reserva crearReserva() {
        return new Reserva("1234", "RES-000001", "Reunion", "Reunion",
                LocalTime.of(8, 0), LocalTime.of(10, 0), LocalDate.of(2026, 11, 10));
    }

    @Test
    void funcionarioNuevoTieneClaveIgualAlId() {
        Funcionario f = new Funcionario("1234", "Ana", "8888-8888");
        assertEquals("1234", f.getClave());
        assertEquals(Usuario.Rol.FUNCIONARIO, f.getRol());
    }

    @Test
    void usuarioSinIdLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Funcionario(" ", "Ana", "8888-8888"));
    }

    @Test
    void reservaConHoraFinalAnteriorLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva("1234", "RES-000001", "Reunion",
                "Reunion", LocalTime.of(10, 0), LocalTime.of(8, 0), LocalDate.of(2026, 11, 10)));
    }

    @Test
    void reservaSinActividadLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva("1234", "RES-000001", "Reunion",
                "", LocalTime.of(8, 0), LocalTime.of(9, 0), LocalDate.of(2026, 11, 10)));
    }

    @Test
    void categoriasConElMismoIdSonIguales() {
        CategoriaRecurso a = new CategoriaRecurso("CAT-000001", "Sala");
        CategoriaRecurso b = new CategoriaRecurso("CAT-000001", "Sala");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void recursosConElMismoIdSonIguales() {
        CategoriaRecurso cat = new CategoriaRecurso("CAT-000001", "Sala");
        assertEquals(new Recurso("R1", cat, "Sala 1"), new Recurso("R1", cat, "Otra descripcion"));
    }

    @Test
    void notificacionSePuedeSerializarYDeserializar() throws Exception {
        Reserva reserva = crearReserva();
        CategoriaRecurso cat = new CategoriaRecurso("CAT-000001", "Sala");
        reserva.getRecursos().add(new Recurso("R1", cat, "Sala 1"));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream salida = new ObjectOutputStream(bytes)) {
            salida.writeObject(new Notificacion(reserva, "Ana"));
        }
        Notificacion leida;
        try (ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            leida = (Notificacion) entrada.readObject();
        }

        assertEquals("Ana", leida.getNombreFuncionario());
        assertEquals("RES-000001", leida.getReserva().getIdReserva());
        assertEquals(1, leida.getReserva().getRecursos().size());
    }

    @Test
    void peticionYRespuestaSeSerializan() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream salida = new ObjectOutputStream(bytes)) {
            salida.writeObject(new Peticion("login", "admin", "1234"));
            salida.writeObject(Respuesta.error("fallo"));
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            Peticion peticion = (Peticion) entrada.readObject();
            Respuesta respuesta = (Respuesta) entrada.readObject();
            assertEquals("login", peticion.getOperacion());
            assertEquals(2, peticion.getParametros().length);
            assertFalse(respuesta.isExito());
            assertEquals("fallo", respuesta.getError());
        }
    }
}
