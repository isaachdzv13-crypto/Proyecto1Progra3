package repository;

import model.CategoriaRecurso;
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
}
