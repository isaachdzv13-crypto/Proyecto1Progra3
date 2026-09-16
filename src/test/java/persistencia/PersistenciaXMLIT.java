package persistencia;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.ListaCategorias;
import repository.ListaFuncionarios;
import repository.ListaRecursos;
import repository.ListaReservas;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenciaXMLIT {


    @TempDir
    Path carpetaTemporal;

    @Test
    void guardaYCargaUsuariosYCategorias() throws Exception {
        Path archivo = carpetaTemporal.resolve("datos.xml");

        Administrador admin = new Administrador("admin", "clave-prueba");

        ListaFuncionarios funcionarios = new ListaFuncionarios();
        funcionarios.addRecurso(
                new Funcionario("9999", "Funcionario Prueba", "8888-9999")
        );

        ListaCategorias categorias = new ListaCategorias();
        categorias.addCategoria(
                new CategoriaRecurso("CAT-000099", "Proyectores")
        );

        PersistenciaXML.guardar(
                archivo,
                admin,
                funcionarios,
                categorias,
                new ListaRecursos(),
                new ListaReservas()
        );

        PersistenciaXML.DatosCargados cargados =
                PersistenciaXML.cargar(archivo);

        assertEquals("admin", cargados.administrador().getId());
        assertEquals("clave-prueba", cargados.administrador().getClave());

        Funcionario funcionario =
                cargados.funcionarios().buscarPorId("9999");

        assertNotNull(funcionario);
        assertEquals("Funcionario Prueba", funcionario.getNombre());
        assertEquals("8888-9999", funcionario.getTelefono());

        CategoriaRecurso categoria =
                cargados.categorias().buscarPorId("CAT-000099");

        assertNotNull(categoria);
        assertEquals("Proyectores", categoria.getDesc());
    }

    @Test
    void conservaRecursoAsignadoYEstadoDeReserva() throws Exception {
        Path archivo = carpetaTemporal.resolve("reservas.xml");

        ListaFuncionarios funcionarios = new ListaFuncionarios();
        funcionarios.addRecurso(
                new Funcionario("1234", "Ana Perez", "8888-1234")
        );

        CategoriaRecurso categoria =
                new CategoriaRecurso("CAT-000050", "Proyectores");

        ListaCategorias categorias = new ListaCategorias();
        categorias.addCategoria(categoria);

        Recurso recurso =
                new Recurso("REC-001", categoria, "Proyector Epson");

        ListaRecursos recursos = new ListaRecursos();
        recursos.addRecurso(recurso);

        Reserva reserva = new Reserva(
                "1234",
                "RES-001",
                "Presentacion del proyecto",
                "Presentacion",
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalDate.of(2026, 10, 1)
        );

        reserva.setRecursos(List.of(recurso));
        reserva.setEstado(Reserva.Estado.CANCELADA);

        ListaReservas reservas = new ListaReservas();
        reservas.add(reserva);

        PersistenciaXML.guardar(
                archivo,
                new Administrador("admin", "1234"),
                funcionarios,
                categorias,
                recursos,
                reservas
        );

        PersistenciaXML.DatosCargados cargados =
                PersistenciaXML.cargar(archivo);

        Recurso recursoCargado =
                cargados.recursos().buscarPorId("REC-001");

        Reserva reservaCargada =
                cargados.reservas().buscarPorId("RES-001");

        assertNotNull(recursoCargado);
        assertNotNull(reservaCargada);
        assertEquals(
                "CAT-000050",
                recursoCargado.getCategoria().getId()
        );
        assertEquals(
                Reserva.Estado.CANCELADA,
                reservaCargada.getEstado()
        );
        assertEquals(1, reservaCargada.getRecursos().size());
        assertSame(
                recursoCargado,
                reservaCargada.getRecursos().get(0)
        );
    }
}
