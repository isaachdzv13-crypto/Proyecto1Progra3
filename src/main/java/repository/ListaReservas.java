package repository;

import model.Recurso;
import model.Reserva;

import java.time.LocalDate;
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
    public String cancelar(String idReserva) {
        Reserva reserva = buscarPorId(idReserva);

        if (reserva == null) {
            return "No existe esa reserva";
        }

        if (reserva.getEstado() == Reserva.Estado.CANCELADA) {
            return "Esa reserva ya estaba cancelada";
        }

        if (!reserva.getFecha().isAfter(LocalDate.now())) {
            return "Solo se pueden cancelar reservas futuras";
        }

        reserva.setEstado(Reserva.Estado.CANCELADA);
        return null;
    }
    public List<Reserva> listarTodas() {
        return new ArrayList<>(listaReservas);
    }

    public boolean tieneReservaActivaConRecurso(Recurso recurso) {
        for (Reserva r : listaReservas) {
            if (r.getEstado() == Reserva.Estado.ACTIVA && r.getRecursos().contains(recurso)) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneReservaActivaDeFuncionario(String idFuncionario) {
        for (Reserva r : listaReservas) {
            if (r.getEstado() == Reserva.Estado.ACTIVA
                    && Objects.equals(r.getIdFuncionario(), idFuncionario)) {
                return true;
            }
        }
        return false;
    }

}
