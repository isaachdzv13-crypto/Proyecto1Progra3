package contexto;

import model.*;
import repository.ListaCategorias;
import repository.ListaFuncionarios;
import repository.ListaRecursos;
import repository.ListaReservas;

public class DatosQuemados {
    private static final DatosQuemados instancia = new DatosQuemados();

    private final ListaFuncionarios funcionarios = new ListaFuncionarios();
    private final ListaCategorias categorias = new ListaCategorias();
    private final ListaRecursos recursos = new ListaRecursos();
    private final ListaReservas reservas = new ListaReservas();

    private final Administrador admin = new Administrador("admin", "1234");

    private Usuario usuarioActual;

    private DatosQuemados() {
        cargarDatosDemo();
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

    /**
     * Valida credenciales contra el administrador único del sistema y
     * contra la lista de funcionarios. Retorna el usuario si coinciden,
     * o null si no hay coincidencia.
     */
    public Usuario login(String id, String clave) {
        if (id.equals(admin.getId()) && clave.equals(admin.getClave())) {
            return admin;
        }

        Funcionario f = funcionarios.buscarPorId(id);
        if (f != null && f.getClave().equals(clave)) {
            return f;
        }

        return null;
    }

    private void cargarDatosDemo() {
        Funcionario juan = new Funcionario("111", "Juan Perez", "3323");
        Funcionario maria = new Funcionario("222", "Maria Perez", "222222");
        funcionarios.addRecurso(juan);
        funcionarios.addRecurso(maria);

        CategoriaRecurso salaGrande = new CategoriaRecurso("Sala para 10 personas");
        CategoriaRecurso laptop = new CategoriaRecurso("Laptop windows");
        CategoriaRecurso salaJuntas = new CategoriaRecurso("Sala de Juntas");
        categorias.addCategoria(salaGrande);
        categorias.addCategoria(laptop);
        categorias.addCategoria(salaJuntas);

        recursos.addRecurso(new Recurso("238715", laptop, "Laptop #238715"));
        recursos.addRecurso(new Recurso("45238", laptop, "Laptop #45238"));
        recursos.addRecurso(new Recurso("34343", salaGrande, "Sala 1 primer piso"));
        recursos.addRecurso(new Recurso("452784", salaJuntas, "Sala de Juntas Principal"));
    }
}
