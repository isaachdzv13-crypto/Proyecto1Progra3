package datos;

import model.CategoriaRecurso;
import model.Recurso;
import model.Reserva;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservaDAO {

    private static final String SELECT =
            "SELECT id, id_funcionario, actividad, descripcion, fecha, hora_inicio, hora_fin, estado FROM reserva ";

    public List<Reserva> listarTodas() {
        return consultar(SELECT + "ORDER BY fecha, hora_inicio, id", null);
    }

    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        return consultar(SELECT + "WHERE id_funcionario = ? ORDER BY fecha, hora_inicio, id", idFuncionario);
    }

    public Reserva buscarPorId(String id) {
        List<Reserva> lista = consultar(SELECT + "WHERE id = ?", id);
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public int contarPorFuncionario(String idFuncionario) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE id_funcionario = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public boolean recursoOcupado(String idRecurso, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        String sql = "SELECT COUNT(*) FROM reserva r JOIN reserva_recurso rr ON rr.id_reserva = r.id "
                + "WHERE rr.id_recurso = ? AND r.estado = 'ACTIVA' AND r.fecha = ? "
                + "AND ? < r.hora_fin AND ? > r.hora_inicio";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idRecurso);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(inicio));
            ps.setTime(4, Time.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public String siguienteId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 5) AS UNSIGNED)) FROM reserva";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int mayor = 0;
            if (rs.next()) {
                mayor = rs.getInt(1);
            }
            return String.format("RES-%06d", mayor + 1);
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void insertar(Reserva reserva) {
        String sqlReserva = "INSERT INTO reserva (id, id_funcionario, actividad, descripcion, fecha, "
                + "hora_inicio, hora_fin, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlRecurso = "INSERT INTO reserva_recurso (id_reserva, id_recurso) VALUES (?, ?)";

        try (Connection con = Conexion.obtener()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlReserva);
                 PreparedStatement psRecurso = con.prepareStatement(sqlRecurso)) {

                ps.setString(1, reserva.getIdReserva());
                ps.setString(2, reserva.getIdFuncionario());
                ps.setString(3, reserva.getActividad());
                ps.setString(4, reserva.getDescripcion());
                ps.setDate(5, Date.valueOf(reserva.getFecha()));
                ps.setTime(6, Time.valueOf(reserva.getHoraInicio()));
                ps.setTime(7, Time.valueOf(reserva.getHoraFin()));
                ps.setString(8, reserva.getEstado().name());
                ps.executeUpdate();

                for (Recurso recurso : reserva.getRecursos()) {
                    psRecurso.setString(1, reserva.getIdReserva());
                    psRecurso.setString(2, recurso.getId());
                    psRecurso.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void cambiarEstado(String idReserva, Reserva.Estado estado) {
        String sql = "UPDATE reserva SET estado = ? WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setString(2, idReserva);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    private List<Reserva> consultar(String sql, String parametro) {
        List<Reserva> lista = new ArrayList<>();
        Map<String, Reserva> porId = new HashMap<>();

        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setString(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva(
                            rs.getString("id_funcionario"),
                            rs.getString("id"),
                            rs.getString("descripcion"),
                            rs.getString("actividad"),
                            rs.getTime("hora_inicio").toLocalTime(),
                            rs.getTime("hora_fin").toLocalTime(),
                            rs.getDate("fecha").toLocalDate());
                    reserva.setEstado(Reserva.Estado.valueOf(rs.getString("estado")));
                    lista.add(reserva);
                    porId.put(reserva.getIdReserva(), reserva);
                }
            }
            cargarRecursos(con, porId);
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
        return lista;
    }

    private void cargarRecursos(Connection con, Map<String, Reserva> porId) throws SQLException {
        if (porId.isEmpty()) {
            return;
        }
        String sql = "SELECT rr.id_reserva, r.id, r.descripcion, c.id AS id_categoria, "
                + "c.descripcion AS desc_categoria FROM reserva_recurso rr "
                + "JOIN recurso r ON r.id = rr.id_recurso JOIN categoria c ON c.id = r.id_categoria "
                + "ORDER BY rr.id_reserva, r.id";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reserva reserva = porId.get(rs.getString("id_reserva"));
                if (reserva != null) {
                    CategoriaRecurso categoria = new CategoriaRecurso(
                            rs.getString("id_categoria"), rs.getString("desc_categoria"));
                    reserva.getRecursos().add(
                            new Recurso(rs.getString("id"), categoria, rs.getString("descripcion")));
                }
            }
        }
    }
}
