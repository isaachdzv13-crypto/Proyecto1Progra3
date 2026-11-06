package accesoDatos;

import model.Funcionario;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServicioFuncionarios extends Servicio{

    private static final String INSERTAR="{call INSERTAR_FUNCIONARIO(?,?,?)}";
    private static final String LISTAR=  "{? = call LISTAR_FUNCIONARIOS()}";
    private static final String BUSCAR=  "{? = call BUSCAR_FUNCIONARIO(?)}";
    private static final String MODIFICAR= "{call MODIFICAR_FUNCIONARIO(?,?,?)}";
    private static final String ELIMINAR= "{call ELIMINAR_FUNCIONARIO(?)}";

    public void insertar(Funcionario funcionario)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta =
                         conexion.prepareCall(INSERTAR)) {

                consulta.setString(1, funcionario.getId());
                consulta.setString(2, funcionario.getNombre());
                consulta.setString(3, funcionario.getTelefono());

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
    public ArrayList<Funcionario> listar()
            throws SQLException, ClassNotFoundException {

        ArrayList<Funcionario> funcionarios = new ArrayList<>();

        conectar();

        try (CallableStatement consulta =
                     conexion.prepareCall(LISTAR)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                while (resultado.next()) {
                    Funcionario funcionario = new Funcionario(
                            resultado.getString("ID"),
                            resultado.getString("NOMBRE"),
                            resultado.getString("TELEFONO")
                    );

                    funcionarios.add(funcionario);
                }
            }

            return funcionarios;

        } finally {
            desconectar();
        }
    }
    public Funcionario buscarPorId(String id)
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
                    return new Funcionario(
                            resultado.getString("ID"),
                            resultado.getString("NOMBRE"),
                            resultado.getString("TELEFONO")
                    );
                }

                return null;
            }

        } finally {
            desconectar();
        }
    }
    public void modificar(Funcionario funcionario)
            throws SQLException, ClassNotFoundException {

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta =
                         conexion.prepareCall(MODIFICAR)) {

                consulta.setString(1, funcionario.getId());
                consulta.setString(2, funcionario.getNombre());
                consulta.setString(3, funcionario.getTelefono());

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


}
