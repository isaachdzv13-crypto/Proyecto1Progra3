package repository;

import model.CategoriaRecurso;

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
//    String consultaCategorias(){
//
//        if(listaCat.isEmpty()){
//            return "La lista esta vacia";
//        }
//         return
//
//
//    }

}
