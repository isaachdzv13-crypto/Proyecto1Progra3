package persistencia;

import model.Administrador;
import model.CategoriaRecurso;
import model.Funcionario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.ListaCategorias;
import repository.ListaFuncionarios;
import repository.ListaRecursos;
import repository.ListaReservas;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PersistenciaXMLGuardarYCargarIT {

    private static final Path ARCHIVO = Path.of("data", "datos.xml");
    private static final Path RESPALDO = Path.of("data", "datos.xml.respaldo");
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
    void guardarYLuegoCargarDevuelveLosMismosDatos() throws Exception {
        Administrador admin = new Administrador("admin", "clave-prueba");

        ListaFuncionarios funcionarios = new ListaFuncionarios();
        funcionarios.addRecurso(new Funcionario("9999", "Funcionario Prueba", "8888-9999"));

        ListaCategorias categorias = new ListaCategorias();
        categorias.addCategoria(new CategoriaRecurso("CAT-000099", "Categoria de prueba"));

        ListaRecursos recursos = new ListaRecursos();
        ListaReservas reservas = new ListaReservas();

        PersistenciaXML.guardar(admin, funcionarios, categorias, recursos, reservas);
        PersistenciaXML.DatosCargados cargados = PersistenciaXML.cargar();

        assertEquals("admin", cargados.administrador().getId());
        assertEquals("clave-prueba", cargados.administrador().getClave());

        Funcionario funcionarioCargado = cargados.funcionarios().buscarPorId("9999");
        assertNotNull(funcionarioCargado);
        assertEquals("Funcionario Prueba", funcionarioCargado.getNombre());

        CategoriaRecurso categoriaCargada = cargados.categorias().buscarPorId("CAT-000099");
        assertNotNull(categoriaCargada);
        assertEquals("Categoria de prueba", categoriaCargada.getDesc());
    }
}
