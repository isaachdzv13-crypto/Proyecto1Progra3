package contexto;

import model.*;
import persistencia.PersistenciaXML;
import repository.ListaCategorias;
import repository.ListaFuncionarios;
import repository.ListaRecursos;
import repository.ListaReservas;

public class DatosQuemados {
    private static final DatosQuemados instancia = new DatosQuemados();

    private ListaFuncionarios funcionarios;
    private ListaCategorias categorias ;
    private ListaRecursos recursos ;
    private ListaReservas reservas ;

    private Administrador admin ;

    private Usuario usuarioActual;

    private DatosQuemados() {
        cargarDatos();
    }

    public static DatosQuemados getInstancia() {
        return instancia;
    }

    public ListaFuncionarios getFuncionarios() {
        return funcionarios;
    }

    public ListaCategorias getCategorias() {
        return categorias;
    }

    public ListaRecursos getRecursos() {
        return recursos;
    }

    public ListaReservas getReservas() {
        return reservas;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }


    public Usuario login(String id, String clave) {
        if (id == null || clave == null) return null;

        if (id.equals(admin.getId()) && clave.equals(admin.getClave())) {
            return admin;
        }

        Funcionario f = funcionarios.buscarPorId(id);
        if (f != null && f.getClave().equals(clave)) {
            return f;
        }

        return null;
    }
    public void guardar() {
        try {
            PersistenciaXML.guardar(admin, funcionarios, categorias, recursos, reservas);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron guardar los datos en XML.", e);
        }
    }
    private void cargarDatos() {
        if (PersistenciaXML.existeArchivo()) {
            try {
                PersistenciaXML.DatosCargados datos = PersistenciaXML.cargar();
                admin = datos.administrador();
                funcionarios = datos.funcionarios();
                categorias = datos.categorias();
                recursos = datos.recursos();
                reservas = datos.reservas();
                return;
            } catch (Exception e) {
                System.err.println("No se pudo leer data/datos.xml: " + e.getMessage());
                System.err.println("Se cargaran los datos de demostracion.");
            }
        }

        cargarDatosDemo();
        guardar();
    }
    private void cargarDatosDemo() {
        funcionarios = new ListaFuncionarios();
        categorias = new ListaCategorias();
        recursos = new ListaRecursos();
        reservas = new ListaReservas();
        admin = new Administrador("admin", "1234");

        Funcionario demo = new Funcionario("1234", "Funcionario Demo", "8888-8888");
        funcionarios.addRecurso(demo);

        CategoriaRecurso salaGrande = new CategoriaRecurso("Sala para 10 personas");
        CategoriaRecurso laptop = new CategoriaRecurso("Laptop windows");
        categorias.addCategoria(salaGrande);
        categorias.addCategoria(laptop);

        recursos.addRecurso(new Recurso("238715", laptop, "Laptop #238715"));
        recursos.addRecurso(new Recurso("34343", salaGrande, "Sala 1 primer piso"));

    }
}
