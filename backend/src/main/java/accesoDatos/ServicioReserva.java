package accesoDatos;

import model.CategoriaRecurso;
import model.Recurso;
import model.Reserva;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServicioReserva extends Servicio {

    private static final String INSERTAR =
            "{call INSERTAR_RESERVA(?, ?, ?, ?, ?, ?, ?)}";

    private static final String ASIGNAR =
            "{call ASIGNAR_RECURSO(?, ?)}";

    private static final String LISTAR =
            "{? = call LISTAR_RESERVAS()}";

    private static final String BUSCAR =
            "{? = call BUSCAR_RESERVA(?)}";

    private static final String POR_FUNCIONARIO =
            "{? = call RESERVAS_POR_FUNCIONARIO(?)}";

    private static final String RECURSOS =
            "{? = call RECURSOS_POR_RESERVA(?)}";

    private static final String CANCELAR =
            "{call CANCELAR_RESERVA(?, ?)}";

    public String insertar(
            String funcionarioId,
            String descripcion,
            String actividad,
            LocalDate fecha,
            LocalTime inicio,
            LocalTime fin,
            List<String> idsRecursos)
            throws SQLException, ClassNotFoundException {

        if (idsRecursos == null || idsRecursos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe asignar al menos un recurso."
            );
        }

        if (fecha == null || inicio == null || fin == null) {
            throw new IllegalArgumentException(
                    "Complete la fecha y las horas."
            );
        }

        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException(
                    "La hora final debe ser posterior a la inicial."
            );
        }

        if (inicio.getSecond() != 0 || fin.getSecond() != 0
                || inicio.getNano() != 0 || fin.getNano() != 0) {

            throw new IllegalArgumentException(
                    "Las horas deben tener precision de minutos."
            );
        }

        ArrayList<String> ordenados = new ArrayList<>();

        for (String id : idsRecursos) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException(
                        "El ID del recurso no puede estar vacio."
                );
            }

            if (ordenados.contains(id)) {
                throw new IllegalArgumentException(
                        "No puede asignar el mismo recurso dos veces."
                );
            }

            ordenados.add(id);
        }

        Collections.sort(ordenados);

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("HH:mm");

        conectar();

        try {
            conexion.setAutoCommit(false);

            String idReserva;

            try (CallableStatement consulta =
                         conexion.prepareCall(INSERTAR)) {

                consulta.setString(1, funcionarioId);
                consulta.setString(2, descripcion);
                consulta.setString(3, actividad);
                consulta.setDate(4, Date.valueOf(fecha));
                consulta.setString(5, inicio.format(formato));
                consulta.setString(6, fin.format(formato));

                consulta.registerOutParameter(7, Types.VARCHAR);

                consulta.execute();

                idReserva = consulta.getString(7);
            }

            try (CallableStatement consulta =
                         conexion.prepareCall(ASIGNAR)) {

                for (String recursoId : ordenados) {
                    consulta.setString(1, idReserva);
                    consulta.setString(2, recursoId);

                    consulta.execute();
                }
            }

            conexion.commit();
            return idReserva;

        } catch (SQLException e) {
            conexion.rollback();
            throw e;

        } finally {
            desconectar();
        }
    }

    public ArrayList<Reserva> listar()
            throws SQLException, ClassNotFoundException {

        conectar();

        try (CallableStatement consulta =
                     conexion.prepareCall(LISTAR)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                return leerReservas(resultado);
            }

        } finally {
            desconectar();
        }
    }

    public Reserva buscarPorId(String id)
            throws SQLException, ClassNotFoundException {

        conectar();

        try (CallableStatement consulta =
                     conexion.prepareCall(BUSCAR)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.setString(2, id);
            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                if (resultado.next()) {
                    return leerReserva(resultado);
                }

                return null;
            }

        } finally {
            desconectar();
        }
    }

    public ArrayList<Reserva> listarPorFuncionario(String funcionarioId)
            throws SQLException, ClassNotFoundException {

        conectar();

        try (CallableStatement consulta =
                     conexion.prepareCall(POR_FUNCIONARIO)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.setString(2, funcionarioId);
            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                return leerReservas(resultado);
            }

        } finally {
            desconectar();
        }
    }

    public ArrayList<Recurso> listarRecursos(String reservaId)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            return cargarRecursos(reservaId);

        } finally {
            desconectar();
        }
    }

    public void cancelar(String reservaId, String funcionarioId)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta =
                         conexion.prepareCall(CANCELAR)) {

                consulta.setString(1, reservaId);
                consulta.setString(2, funcionarioId);

                consulta.execute();
            }

            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;

        } finally {
            desconectar();
        }
    }

    private Reserva leerReserva(ResultSet resultado)
            throws SQLException {

        Reserva reserva = new Reserva(
                resultado.getString("FUNCIONARIO_ID"),
                resultado.getString("ID"),
                resultado.getString("DESCRIPCION"),
                resultado.getString("ACTIVIDAD"),
                LocalTime.parse(resultado.getString("HORA_INICIO")),
                LocalTime.parse(resultado.getString("HORA_FIN")),
                resultado.getDate("FECHA").toLocalDate()
        );

        reserva.setEstado(
                Reserva.Estado.valueOf(resultado.getString("ESTADO"))
        );

        reserva.setRecursos(
                cargarRecursos(reserva.getIdReserva())
        );

        return reserva;
    }

    private ArrayList<Reserva> leerReservas(ResultSet resultado)
            throws SQLException {

        ArrayList<Reserva> reservas = new ArrayList<>();

        while (resultado.next()) {
            reservas.add(leerReserva(resultado));
        }

        return reservas;
    }

    private ArrayList<Recurso> cargarRecursos(String reservaId)
            throws SQLException {

        ArrayList<Recurso> recursos = new ArrayList<>();

        try (CallableStatement consulta =
                     conexion.prepareCall(RECURSOS)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.setString(2, reservaId);
            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                while (resultado.next()) {
                    CategoriaRecurso categoria = new CategoriaRecurso(
                            resultado.getString("CATEGORIA_ID"),
                            resultado.getString("CATEGORIA_DESCRIPCION")
                    );

                    Recurso recurso = new Recurso(
                            resultado.getString("ID"),
                            categoria,
                            resultado.getString("DESCRIPCION")
                    );

                    recursos.add(recurso);
                }
            }
        }

        return recursos;
    }
}