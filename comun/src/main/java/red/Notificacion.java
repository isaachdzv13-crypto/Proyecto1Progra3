package red;

import model.Reserva;

import java.io.Serializable;

public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Reserva reserva;
    private final String nombreFuncionario;

    public Notificacion(Reserva reserva, String nombreFuncionario) {
        this.reserva = reserva;
        this.nombreFuncionario = nombreFuncionario;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public String getNombreFuncionario() {
        return nombreFuncionario;
    }
}
