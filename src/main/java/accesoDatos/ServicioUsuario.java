package accesoDatos;

import java.sql.CallableStatement;
import java.sql.SQLException;

public class ServicioUsuario extends Servicio {

    private static final String CAMBIAR_CLAVE =
            "{call CAMBIAR_CLAVE(?, ?, ?)}";

    public void cambiarClave(
            String id,
            String claveActual,
            String claveNueva)
            throws SQLException, ClassNotFoundException {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe indicar el ID del usuario."
            );
        }

        if (claveActual == null || claveActual.isBlank()
                || claveNueva == null || claveNueva.isBlank()) {

            throw new IllegalArgumentException(
                    "Las contraseñas no pueden estar vacias."
            );
        }

        conectar();

        try {
            conexion.setAutoCommit(false);

            try (CallableStatement consulta =
                         conexion.prepareCall(CAMBIAR_CLAVE)) {

                consulta.setString(1, id);
                consulta.setString(2, claveActual);
                consulta.setString(3, claveNueva);

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