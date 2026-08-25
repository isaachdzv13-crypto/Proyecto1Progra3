package modelo;

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
    void addCategoria(CategoriaRecurso c){
        listaCat.add(c);
    }
    boolean borrarCategoria(String desc){
        CategoriaRecurso borr = busquedadDescripcion(desc);

        if (borr != null) {
            listaCat.remove(borr);
            return true;
        }

        return false;

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
