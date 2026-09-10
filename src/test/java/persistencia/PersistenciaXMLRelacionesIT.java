package persistencia;

import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.ListaCategorias;
import repository.ListaFuncionarios;
import repository.ListaRecursos;
import repository.ListaReservas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenciaXMLRelacionesIT {

    private static final Path ARCHIVO = Path.of("data", "datos.xml");
    private static final Path RESPALDO = Path.of("data", "datos.xml.respaldo2");
    private static boolean habiaArchivoOriginal;

    @BeforeAll
    static void respaldarArchivoOriginal() throws Exception {
        habiaArchivoOriginal = Files.exists(ARCHIVO);
        if (habiaArchivoOriginal) {
            Files.copy(ARCHIVO, RESPALDO);
        }
    }

    @AfterAll
    static void restaurarArchivoOriginal() throws Exception {
        if (habiaArchivoOriginal) {
            Files.copy(RESPALDO, ARCHIVO, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(RESPALDO);
        } else {
            Files.deleteIfExists(ARCHIVO);
        }
    }

    @Test
    void relacionesEntreRecursoCategoriaYReservaSePreservan() throws Exception {
        Administrador admin = new Administrador("admin", "1234");
        ListaFuncionarios funcionarios = new ListaFuncionarios();
        Funcionario funcionario = new Funcionario("5555", "Funcionario Reserva", "8888-5555");
        funcionarios.addRecurso(funcionario);

        ListaCategorias categorias = new ListaCategorias();
        CategoriaRecurso categoria = new CategoriaRecurso("CAT-000050", "Equipos audiovisuales");
        categorias.addCategoria(categoria);

        ListaRecursos recursos = new ListaRecursos();
        Recurso proyector = new Recurso("REC-REL01", categoria, "Proyector Epson");
        recursos.addRecurso(proyector);

        ListaReservas reservas = new ListaReservas();
        Reserva reserva = new Reserva("5555", "RES-REL01", "Presentacion final", "Presentacion",
                LocalTime.of(14, 0), LocalTime.of(15, 0), LocalDate.now().plusDays(3));
        reserva.setRecursos(List.of(proyector));
        reservas.add(reserva);

        PersistenciaXML.guardar(admin, funcionarios, categorias, recursos, reservas);
        PersistenciaXML.DatosCargados cargados = PersistenciaXML.cargar();

        Recurso proyectorCargado = cargados.recursos().buscarPorId("REC-REL01");
        assertNotNull(proyectorCargado);
        assertEquals("CAT-000050", proyectorCargado.getCategoria().getId(),
                "El recurso cargado deberia seguir apuntando a su categoria original");

        Reserva reservaCargada = cargados.reservas().buscarPorId("RES-REL01");
        assertNotNull(reservaCargada);
        assertEquals(1, reservaCargada.getRecursos().size());
        assertEquals("REC-REL01", reservaCargada.getRecursos().get(0).getId());
    }
}
