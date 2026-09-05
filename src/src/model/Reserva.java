package model;



import javax.swing.*;
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
        return actividad;
    }

    public void setactividad(String actividad) {
        actividad = actividad;
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
    private final String actividad;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fecha;
    private List<Recurso> listaRecursos;

    public Estado getEstado() {
        return estado;
    }
    public void setEstado(Estado s){this.estado=s ;
    }

    public enum Estado { ACTIVA, CANCELADA }
    private Estado estado = Estado.ACTIVA;

    public Reserva(String idFuncionario, String idReserva,String descripcion,String actividad, LocalTime inicio, LocalTime hFinal, LocalDate fecha)  {

        if (idFuncionario == null || idFuncionario.isBlank()) {
            throw new IllegalArgumentException("El ID del funcionario es obligatorio");
        }

        if (idReserva == null || idReserva.isBlank()) {
            throw new IllegalArgumentException("El ID de la reserva es obligatorio");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }

        if (actividad == null || actividad.isBlank()) {
            throw new IllegalArgumentException("La actividad es obligatoria");
        }

        if (inicio == null || hFinal == null) {
            throw new IllegalArgumentException("Las horas son obligatorias");
        }

        if (!hFinal.isAfter(inicio)) {
            throw new IllegalArgumentException(
                    "La hora final debe ser posterior a la hora de inicio"
            );
        }

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        this.idFuncionario=idFuncionario;
        this.idReserva=idReserva;
        this.descripcion=descripcion;
        this.actividad=actividad;
        this.horaInicio=inicio;
        this.horaFin=hFinal;
        this.fecha=fecha;
        
    }
    public void ingresarRecursoAReserva(Recurso r){

        if(listaRecursos.contains(r)) {
            this.listaRecursos.add(r);
        }
        else JOptionPane.showMessageDialog(null,"No se encontro el recurso.");
    }

    public void liberarRecursos(){
        for(Recurso r: this.listaRecursos){
            listaRecursos.remove(r);

        }
    }

}
