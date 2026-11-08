package accesoDatos;

import model.CategoriaRecurso;
import model.Recurso;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServicioRecurso extends Servicio {

    private static final String INSERTAR =
            "{call INSERTAR_RECURSO(?, ?, ?)}";

    private static final String LISTAR =
            "{? = call LISTAR_RECURSOS()}";

    private static final String BUSCAR =
            "{? = call BUSCAR_RECURSO(?)}";

    private static final String POR_CATEGORIA =
            "{? = call LISTAR_RECURSOS_POR_CATEGORIA(?)}";

    private static final String MODIFICAR =
            "{call MODIFICAR_RECURSO(?, ?, ?)}";

    private static final String ELIMINAR =
            "{call ELIMINAR_RECURSO(?)}";

    private static final String DISPONIBLES =
            "{? = call RECURSOS_DISPONIBLES(?, ?, ?, ?)}";

    public void insertar(Recurso recurso)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta =
                         conexion.prepareCall(INSERTAR)) {

                consulta.setString(1, recurso.getId());
                consulta.setString(2, recurso.getCategoria().getId());
                consulta.setString(3, recurso.getDescripcion());

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

    public ArrayList<Recurso> listar()
            throws SQLException, ClassNotFoundException {

        conectar();

        try (CallableStatement consulta =
                     conexion.prepareCall(LISTAR)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                return leerRecursos(resultado);
            }

        } finally {
            desconectar();
        }
    }

    public Recurso buscarPorId(String id)
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
                    return leerRecurso(resultado);
                }

                return null;
            }

        } finally {
            desconectar();
        }
    }

    public ArrayList<Recurso> listarPorCategoria(String categoriaId)
            throws SQLException, ClassNotFoundException {

        conectar();

        try (CallableStatement consulta =
                     conexion.prepareCall(POR_CATEGORIA)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.setString(2, categoriaId);
            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                return leerRecursos(resultado);
            }

        } finally {
            desconectar();
        }
    }

    public void modificar(Recurso recurso)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta =
                         conexion.prepareCall(MODIFICAR)) {

                consulta.setString(1, recurso.getId());
                consulta.setString(2, recurso.getCategoria().getId());
                consulta.setString(3, recurso.getDescripcion());

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

    public void eliminar(String id)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta =
                         conexion.prepareCall(ELIMINAR)) {

                consulta.setString(1, id);
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

    public ArrayList<Recurso> disponibles(
            String categoriaId,
            LocalDate fecha,
            LocalTime inicio,
            LocalTime fin)
            throws SQLException, ClassNotFoundException {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("HH:mm");

        conectar();

        try (CallableStatement consulta =
                     conexion.prepareCall(DISPONIBLES)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.setString(2, categoriaId);
            consulta.setDate(3, Date.valueOf(fecha));
            consulta.setString(4, inicio.format(formato));
            consulta.setString(5, fin.format(formato));

            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                return leerRecursos(resultado);
            }

        } finally {
            desconectar();
        }
    }

    private Recurso leerRecurso(ResultSet resultado)
            throws SQLException {

        CategoriaRecurso categoria = new CategoriaRecurso(
                resultado.getString("CATEGORIA_ID"),
                resultado.getString("CATEGORIA_DESCRIPCION")
        );

        return new Recurso(
                resultado.getString("ID"),
                categoria,
                resultado.getString("DESCRIPCION")
        );
    }

    private ArrayList<Recurso> leerRecursos(ResultSet resultado)
            throws SQLException {

        ArrayList<Recurso> recursos = new ArrayList<>();

        while (resultado.next()) {
            recursos.add(leerRecurso(resultado));
        }

        return recursos;
    }
}
