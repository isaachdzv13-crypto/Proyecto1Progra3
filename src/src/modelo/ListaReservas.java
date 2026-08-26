package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaReservas {
    private List<Reserva> listaReservas;
    public ListaReservas(){
        listaReservas= new ArrayList<>();
    }

    public void add(Reserva r){
        listaReservas.add(r);
    }

public ListaReservas reservasPorId(String id){
        ListaReservas rs = new ListaReservas();
        for(Reserva s: this.listaReservas){
            if(Objects.equals(s.getIdFuncionario(), id)){
                rs.add(s);
            }
        }

        return rs;

}
}
