package datos;

import model.CategoriaRecurso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<CategoriaRecurso> listar() {
        return buscarPorDescripcion("");
    }

    public List<CategoriaRecurso> buscarPorDescripcion(String desc) {
        String sql = "SELECT id, descripcion FROM categoria WHERE LOWER(descripcion) LIKE ? ORDER BY id";
        List<CategoriaRecurso> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String texto = desc == null ? "" : desc.trim().toLowerCase();
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new CategoriaRecurso(rs.getString("id"), rs.getString("descripcion")));
                }
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
        return lista;
    }

    public CategoriaRecurso buscarPorId(String id) {
        String sql = "SELECT id, descripcion FROM categoria WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CategoriaRecurso(rs.getString("id"), rs.getString("descripcion"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public CategoriaRecurso insertar(String desc) {
        String id = siguienteId();
        String sql = "INSERT INTO categoria (id, descripcion) VALUES (?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, desc);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
        return new CategoriaRecurso(id, desc);
    }

    public void actualizar(CategoriaRecurso c) {
        String sql = "UPDATE categoria SET descripcion = ? WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDesc());
            ps.setString(2, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void borrar(String id) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    private String siguienteId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 5) AS UNSIGNED)) FROM categoria";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int mayor = 0;
            if (rs.next()) {
                mayor = rs.getInt(1);
            }
            return String.format("CAT-%06d", mayor + 1);
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }
}
