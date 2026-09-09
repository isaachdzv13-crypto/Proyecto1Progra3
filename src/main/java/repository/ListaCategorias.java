package repository;

import model.CategoriaRecurso;
import model.Funcionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaCategorias {
    private final List<CategoriaRecurso> listaCat;

    public ListaCategorias() {
        this.listaCat= new ArrayList<>();
    }

    public  CategoriaRecurso busquedadDescripcion(String desc){

        for(CategoriaRecurso c : listaCat){

            if(Objects.equals(c.getDesc(), desc)){
                return c;
            }

        }

    return null;

    }
    public void addCategoria(CategoriaRecurso c){
        listaCat.add(c);
    }
    public boolean borrarCategoria(String desc){
        CategoriaRecurso borr = busquedadDescripcion(desc);

        if (borr != null) {
            listaCat.remove(borr);
            return true;
        }

        return false;

    }
    public CategoriaRecurso buscarPorId(String id){
        for (CategoriaRecurso c : listaCat){
            if (Objects.equals(c.getId(), id)) return c;
        }
        return null;
    }
    public List<CategoriaRecurso> listarTodas() {
        return new ArrayList<>(listaCat);
    }

    public List<CategoriaRecurso> buscarPorDescripcion(String desc) {
        List<CategoriaRecurso> res = new ArrayList<>();
        for (CategoriaRecurso c : listaCat){
            if (desc == null || desc.isBlank()
                    || c.getDesc().toLowerCase().contains(desc.toLowerCase())){
                res.add(c);
            }
        }
        return res;

    }
}
