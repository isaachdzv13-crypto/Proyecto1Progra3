package repository;

import model.Reserva;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaReservas {
    private final List<Reserva> listaReservas;
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
    public List<Reserva> listarPorFuncionario(String idFuncionario){
        List<Reserva> res = new ArrayList<>();
        for (Reserva r : listaReservas){
            if (Objects.equals(r.getIdFuncionario(), idFuncionario)) res.add(r);
        }
        return res;
    }

    public Reserva buscarPorId(String idReserva){
        for (Reserva r : listaReservas){
            if (Objects.equals(r.getIdReserva(), idReserva)) return r;
        }
        return null;
    }
    public String cancelar(String idReserva){
        Reserva r = buscarPorId(idReserva);
        if (r == null) return "No existe esa reserva";
        if (r.getEstado() == Reserva.Estado.CANCELADA) return "Esa reserva ya estaba cancelada";
        if (!r.getFecha().isAfter(java.time.LocalDate.now())) return "Solo se pueden cancelar reservas futuras";
        r.setEstado(Reserva.Estado.CANCELADA);
        return null;
    }



}
