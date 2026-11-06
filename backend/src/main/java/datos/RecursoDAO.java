package datos;

import model.CategoriaRecurso;
import model.Recurso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    private static final String SELECT =
            "SELECT r.id, r.descripcion, c.id AS id_categoria, c.descripcion AS desc_categoria "
                    + "FROM recurso r JOIN categoria c ON c.id = r.id_categoria ";

    public List<Recurso> listar() {
        return buscar(null, null);
    }

    public List<Recurso> buscar(String idCategoria, String descripcion) {
        String sql = SELECT + "WHERE (? IS NULL OR r.id_categoria = ?) "
                + "AND LOWER(r.descripcion) LIKE ? ORDER BY r.id";
        List<Recurso> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String cat = (idCategoria == null || idCategoria.isBlank()) ? null : idCategoria;
            String texto = descripcion == null ? "" : descripcion.trim().toLowerCase();
            ps.setString(1, cat);
            ps.setString(2, cat);
            ps.setString(3, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(crearRecurso(rs));
                }
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
        return lista;
    }

    public Recurso buscarPorId(String id) {
        String sql = SELECT + "WHERE r.id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearRecurso(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void insertar(Recurso r) {
        String sql = "INSERT INTO recurso (id, id_categoria, descripcion) VALUES (?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getId());
            ps.setString(2, r.getCategoria().getId());
            ps.setString(3, r.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void actualizar(Recurso r) {
        String sql = "UPDATE recurso SET id_categoria = ?, descripcion = ? WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getCategoria().getId());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void borrar(String id) {
        String sql = "DELETE FROM recurso WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public int contarPorCategoria(String idCategoria) {
        return contar("SELECT COUNT(*) FROM recurso WHERE id_categoria = ?", idCategoria);
    }

    public int contarReservas(String idRecurso) {
        return contar("SELECT COUNT(*) FROM reserva_recurso WHERE id_recurso = ?", idRecurso);
    }

    private int contar(String sql, String parametro) {
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    private Recurso crearRecurso(ResultSet rs) throws SQLException {
        CategoriaRecurso categoria = new CategoriaRecurso(
                rs.getString("id_categoria"), rs.getString("desc_categoria"));
        return new Recurso(rs.getString("id"), categoria, rs.getString("descripcion"));
    }
}
