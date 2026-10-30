package accesoDatos;

import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicioLogin extends Servicio {

    private static final String LOGIN =
            "{? = call LOGIN_USUARIO(?, ?)}";

    public String login(String id, String clave)
            throws SQLException, ClassNotFoundException {

        conectar();

        try (CallableStatement consulta = conexion.prepareCall(LOGIN)) {

            consulta.registerOutParameter(1, OracleTypes.CURSOR);
            consulta.setString(2, id);
            consulta.setString(3, clave);

            consulta.execute();

            try (ResultSet resultado =
                         (ResultSet) consulta.getObject(1)) {

                if (resultado.next()) {
                    return resultado.getString("ROL");
                }

                return null;
            }

        } finally {
            desconectar();
        }
    }
}
