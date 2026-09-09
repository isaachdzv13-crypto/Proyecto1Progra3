package repository;

import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaRecursos {
    private final List<Recurso> listaRecu;

    public ListaRecursos(){
        listaRecu= new ArrayList<>();

    }


    public void addRecurso(Recurso recurso) {
        listaRecu.add(recurso);
    }

    public Recurso buscarPorId(String id) {

        for (Recurso r : listaRecu) {

            if (Objects.equals(r.getId(), id)) {
                return r;
            }
        }

        return null;
    }
    public boolean borrarRecurso(String id) {

        Recurso recurso = buscarPorId(id);

        if (recurso != null) {
            listaRecu.remove(recurso);
            return true;
        }

        return false;
    }
    public List<Recurso> filtrarPorCategoria(CategoriaRecurso categoria) {

        List<Recurso> resultado = new ArrayList<>();

        for (Recurso r : listaRecu) {

            if (r.getCategoria().equals(categoria)) {
                resultado.add(r);
            }
        }

        return resultado;
    }

    public List<Recurso> listarTodos() {
        return new ArrayList<>(listaRecu);
    }

    public List<Recurso> buscar(CategoriaRecurso categoria, String descripcion) {
        List<Recurso> resultado = new ArrayList<>();
        for (Recurso r : listaRecu) {
            boolean coincideCategoria = categoria == null
                    || Objects.equals(r.getCategoria().getId(), categoria.getId());
            boolean coincideDescripcion = descripcion == null || descripcion.isBlank()
                    || r.getDescripcion().toLowerCase().contains(descripcion.toLowerCase());
            if (coincideCategoria && coincideDescripcion) {
                resultado.add(r);
            }
        }
        return resultado;
    }
}
