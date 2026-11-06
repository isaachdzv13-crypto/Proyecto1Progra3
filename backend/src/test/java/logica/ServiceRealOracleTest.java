package logica;

import org.junit.jupiter.api.Test;
import red.ServiceException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceRealOracleTest {

    @Test
    void erroresDeProcedimientoSeLimpianSinPrefijoOra() {
        SQLException e = new SQLException(
                "ORA-20003: No existe ese recurso\nORA-06512: at \"SYSTEM.ELIMINAR_RECURSO\", line 8", "72000", 20003);
        ServiceException traducido = ServiceReal.traducir(e);
        assertEquals("No existe ese recurso", traducido.getMessage());
    }

    @Test
    void borrarConRegistrosAsociadosDaMensajeAmigable() {
        SQLException e = new SQLException("ORA-02292: integrity constraint violated", "23000", 2292);
        assertEquals("No se puede borrar: hay registros asociados.", ServiceReal.traducir(e).getMessage());
    }

    @Test
    void duplicadoDaMensajeAmigable() {
        SQLException e = new SQLException("ORA-00001: unique constraint violated", "23000", 1);
        assertEquals("Ya existe un registro con ese identificador.", ServiceReal.traducir(e).getMessage());
    }

    @Test
    void otroErrorConservaElDetalle() {
        SQLException e = new SQLException("ORA-12541: no listener", "08006", 12541);
        assertEquals("Error en la base de datos: ORA-12541: no listener", ServiceReal.traducir(e).getMessage());
    }
}
