package accesoDatos;

import model.CategoriaRecurso;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServicioCategoria extends Servicio {

    private static final String INSERTAR =
            "{call INSERTAR_CATEGORIA(?)}";

    private static final String LISTAR =
            "{? = call LISTAR_CATEGORIAS()}";

    private static final String BUSCAR =
            "{? = call BUSCAR_CATEGORIAS(?)}";

    private static final String MODIFICAR =
            "{call MODIFICAR_CATEGORIA(?, ?)}";

    private static final String ELIMINAR =
            "{call ELIMINAR_CATEGORIA(?)}";

    public void insertar(String descripcion)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta = conexion.prepareCall(INSERTAR)) {
                consulta.setString(1, descripcion);
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

    public ArrayList<CategoriaRecurso> listar()
            throws SQLException, ClassNotFoundException {

        ArrayList<CategoriaRecurso> categorias = new ArrayList<>();
        conectar();

        try (CallableStatement consulta = conexion.prepareCall(LISTAR)) {
            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.execute();

            try (ResultSet resultado = (ResultSet) consulta.getObject(1)) {
                while (resultado.next()) {
                    CategoriaRecurso categoria = new CategoriaRecurso(
                            resultado.getString("ID"),
                            resultado.getString("DESCRIPCION")
                    );
                    categorias.add(categoria);
                }
            }

            return categorias;

        } finally {
            desconectar();
        }
    }

    public ArrayList<CategoriaRecurso> buscarPorDescripcion(String descripcion)
            throws SQLException, ClassNotFoundException {

        ArrayList<CategoriaRecurso> categorias = new ArrayList<>();
        conectar();

        try (CallableStatement consulta = conexion.prepareCall(BUSCAR)) {
            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.setString(2, descripcion);
            consulta.execute();

            try (ResultSet resultado = (ResultSet) consulta.getObject(1)) {
                while (resultado.next()) {
                    CategoriaRecurso categoria = new CategoriaRecurso(
                            resultado.getString("ID"),
                            resultado.getString("DESCRIPCION")
                    );
                    categorias.add(categoria);
                }
            }

            return categorias;

        } finally {
            desconectar();
        }
    }

    public void modificar(CategoriaRecurso categoria)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta = conexion.prepareCall(MODIFICAR)) {
                consulta.setString(1, categoria.getId());
                consulta.setString(2, categoria.getDesc());
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

            try (CallableStatement consulta = conexion.prepareCall(ELIMINAR)) {
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
}

