package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
//SOLO SE PUEDE CREAR SI ES FUNCIONARIO
public class Reserva {

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = recursos;
    }

    private List<Recurso> recursos;

    public String getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActividad() {
        return Actividad;
    }

    public void setActividad(String actividad) {
        Actividad = actividad;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    private String idFuncionario;
    private String idReserva;
    private String descripcion;
    private String Actividad;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fecha;
    private List<Recurso> listaRecursos;

    public Reserva(String idFuncionario, String idReserva,String descripcion,String actvidad, LocalTime inicio, LocalTime hFinal, LocalDate fecha){
        this.idFuncionario=idFuncionario;
        this.idReserva=idReserva;
        this.descripcion=descripcion;
        this.Actividad=actvidad;
        this.horaInicio=inicio;
        this.horaFin=hFinal;
        this.fecha=fecha;


    }

}
