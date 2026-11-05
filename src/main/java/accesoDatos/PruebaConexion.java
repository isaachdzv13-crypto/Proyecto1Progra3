package accesoDatos;

import model.CategoriaRecurso;
import model.Funcionario;
import model.Recurso;
import model.Reserva;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class PruebaConexion extends Servicio {

    public static void main(String[] args) throws Exception {

        ServicioFuncionarios funcionarios = new ServicioFuncionarios();
        ServicioCategoria categorias = new ServicioCategoria();
        ServicioRecurso recursos = new ServicioRecurso();
        ServicioReserva reservas = new ServicioReserva();
        ServicioUsuario usuarios = new ServicioUsuario();
        ServicioLogin logueo = new ServicioLogin();

        // Identificadores diferentes en cada ejecucion.
        String numero = String.valueOf(System.currentTimeMillis());

        String funcionarioId = "FUN-" + numero;
        String recursoId = "REC-" + numero;
        String recursoTemporalId = "TMP-" + numero;
        String descripcionCategoria = "Categoria prueba " + numero;

        // 1. Crear funcionario y categoria de prueba.

        Funcionario funcionario = new Funcionario(
                funcionarioId,
                "Funcionario de prueba",
                "8888-9999"
        );

        funcionarios.insertar(funcionario);

        categorias.insertar(descripcionCategoria);

        ArrayList<CategoriaRecurso> encontradas =
                categorias.buscarPorDescripcion(descripcionCategoria);

        comprobar(
                encontradas.size() == 1,
                "Se encontro la categoria de prueba."
        );

        CategoriaRecurso categoria = encontradas.get(0);

        // 2. Probar cambio de contraseña.
        // La contraseña inicial del funcionario es su ID.

        String claveNueva = "ClavePrueba123";

        usuarios.cambiarClave(
                funcionarioId,
                funcionarioId,
                claveNueva
        );

        comprobar(
                "FUNCIONARIO".equals(
                        logueo.login(funcionarioId, claveNueva)
                ),
                "La nueva contraseña funciona."
        );

        comprobar(
                logueo.login(funcionarioId, funcionarioId) == null,
                "La contraseña anterior ya no funciona."
        );

        // Recuperar la contraseña inicial del usuario de prueba.
        usuarios.cambiarClave(
                funcionarioId,
                claveNueva,
                funcionarioId
        );

        comprobar(
                "FUNCIONARIO".equals(
                        logueo.login(funcionarioId, funcionarioId)
                ),
                "La contraseña inicial quedo restaurada."
        );

        // 3. Probar insertar, buscar y modificar recursos.

        Recurso temporal = new Recurso(
                recursoTemporalId,
                categoria,
                "Recurso temporal"
        );

        recursos.insertar(temporal);

        Recurso encontrado = recursos.buscarPorId(recursoTemporalId);

        comprobar(
                encontrado != null,
                "El recurso insertado se puede buscar."
        );

        encontrado.setDescripcion("Recurso modificado");
        recursos.modificar(encontrado);

        encontrado = recursos.buscarPorId(recursoTemporalId);

        comprobar(
                encontrado != null
                        && "Recurso modificado".equals(
                        encontrado.getDescripcion()
                ),
                "La descripcion del recurso se modifico."
        );

        // 4. Probar listar recursos.

        boolean aparece = false;

        for (Recurso recurso : recursos.listar()) {
            if (recursoTemporalId.equals(recurso.getId())) {
                aparece = true;
            }
        }

        comprobar(aparece, "El recurso aparece en la lista general.");

        aparece = false;

        for (Recurso recurso :
                recursos.listarPorCategoria(categoria.getId())) {

            if (recursoTemporalId.equals(recurso.getId())) {
                aparece = true;
            }
        }

        comprobar(aparece, "El recurso aparece en su categoria.");

        // 5. Probar eliminar un recurso sin reservas.

        recursos.eliminar(recursoTemporalId);

        comprobar(
                recursos.buscarPorId(recursoTemporalId) == null,
                "El recurso temporal se elimino."
        );

        // 6. Crear el recurso que usaremos en la reserva.

        Recurso recursoReserva = new Recurso(
                recursoId,
                categoria,
                "Recurso para probar reservas"
        );

        recursos.insertar(recursoReserva);

        LocalDate fecha = LocalDate.now().plusDays(30);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(12, 0);

        ArrayList<Recurso> disponibles = recursos.disponibles(
                categoria.getId(),
                fecha,
                inicio,
                fin
        );

        comprobar(
                disponibles.size() == 1
                        && recursoId.equals(disponibles.get(0).getId()),
                "El recurso esta disponible antes de reservar."
        );

        // 7. Insertar una reserva con ese recurso.

        ArrayList<String> idsRecursos = new ArrayList<>();
        idsRecursos.add(recursoId);

        String reservaId = reservas.insertar(
                funcionarioId,
                "Reserva creada desde la prueba JDBC",
                "Reunion de prueba",
                fecha,
                inicio,
                fin,
                idsRecursos
        );

        System.out.println("ID de reserva generado: " + reservaId);

        Reserva reserva = reservas.buscarPorId(reservaId);

        comprobar(
                reserva != null
                        && reserva.getEstado() == Reserva.Estado.ACTIVA,
                "La reserva se guardo como ACTIVA."
        );

        comprobar(
                reserva.getRecursos().size() == 1
                        && recursoId.equals(
                        reserva.getRecursos().get(0).getId()
                ),
                "La reserva contiene el recurso asignado."
        );

        ArrayList<Recurso> asignados =
                reservas.listarRecursos(reservaId);

        comprobar(
                asignados.size() == 1
                        && recursoId.equals(asignados.get(0).getId()),
                "La consulta de recursos por reserva funciona."
        );

        // 8. Probar listar reservas.

        aparece = false;

        for (Reserva r : reservas.listar()) {
            if (reservaId.equals(r.getIdReserva())) {
                aparece = true;
            }
        }

        comprobar(aparece, "La reserva aparece en la lista general.");

        comprobar(
                reservas.listarPorFuncionario(funcionarioId).size() == 1,
                "El funcionario tiene una reserva."
        );

        // 9. El recurso ya no debe estar disponible en ese horario.

        disponibles = recursos.disponibles(
                categoria.getId(),
                fecha,
                inicio,
                fin
        );

        comprobar(
                disponibles.isEmpty(),
                "El recurso reservado ya no aparece disponible."
        );

        // 10. Intentar reservarlo nuevamente en el mismo horario.
        // Debe fallar y deshacer la segunda reserva completa.

        boolean conflictoDetectado = false;

        try {
            reservas.insertar(
                    funcionarioId,
                    "Reserva que debe rechazarse",
                    "Actividad en conflicto",
                    fecha,
                    inicio,
                    fin,
                    idsRecursos
            );

        } catch (SQLException e) {
            if (e.getErrorCode() != 20018) {
                throw e;
            }

            conflictoDetectado = true;
        }

        comprobar(
                conflictoDetectado,
                "Oracle rechazo la reserva con horario ocupado."
        );

        comprobar(
                reservas.listarPorFuncionario(funcionarioId).size() == 1,
                "El rollback evito guardar una reserva incompleta."
        );

        // 11. Cancelar la reserva original.

        reservas.cancelar(reservaId, funcionarioId);

        reserva = reservas.buscarPorId(reservaId);

        comprobar(
                reserva != null
                        && reserva.getEstado() == Reserva.Estado.CANCELADA,
                "La reserva quedo CANCELADA."
        );

        // 12. El recurso debe volver a estar disponible.

        disponibles = recursos.disponibles(
                categoria.getId(),
                fecha,
                inicio,
                fin
        );

        comprobar(
                disponibles.size() == 1
                        && recursoId.equals(disponibles.get(0).getId()),
                "El recurso volvio a estar disponible."
        );

        System.out.println();
        System.out.println("TODAS LAS PRUEBAS TERMINARON CORRECTAMENTE.");
    }

    private static void comprobar(boolean condicion, String mensaje) {

        if (!condicion) {
            throw new IllegalStateException("FALLO: " + mensaje);
        }

        System.out.println("OK: " + mensaje);
    }
}