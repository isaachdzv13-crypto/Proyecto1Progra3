package datos;

import model.Administrador;
import model.Funcionario;
import model.Usuario;
import red.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario buscarPorId(String id) {
        String sql = "SELECT id, clave, rol, nombre, telefono FROM usuario WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearUsuario(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public Funcionario buscarFuncionario(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario instanceof Funcionario) {
            return (Funcionario) usuario;
        }
        return null;
    }

    public List<Funcionario> listarFuncionarios() {
        return buscarPorNombre("");
    }

    public List<Funcionario> buscarPorNombre(String nombre) {
        String sql = "SELECT id, clave, rol, nombre, telefono FROM usuario "
                + "WHERE rol = 'FUNCIONARIO' AND LOWER(nombre) LIKE ? ORDER BY id";
        List<Funcionario> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String texto = nombre == null ? "" : nombre.trim().toLowerCase();
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add((Funcionario) crearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
        return lista;
    }

    public void insertarFuncionario(Funcionario f) {
        String sql = "INSERT INTO usuario (id, clave, rol, nombre, telefono) VALUES (?, ?, 'FUNCIONARIO', ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getId());
            ps.setString(2, f.getClave());
            ps.setString(3, f.getNombre());
            ps.setString(4, f.getTelefono());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void actualizarFuncionario(Funcionario f) {
        String sql = "UPDATE usuario SET nombre = ?, telefono = ? WHERE id = ? AND rol = 'FUNCIONARIO'";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNombre());
            ps.setString(2, f.getTelefono());
            ps.setString(3, f.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void borrarFuncionario(String id) {
        String sql = "DELETE FROM usuario WHERE id = ? AND rol = 'FUNCIONARIO'";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    public void actualizarClave(String id, String claveNueva) {
        String sql = "UPDATE usuario SET clave = ? WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, claveNueva);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw Conexion.error(e);
        }
    }

    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        String rol = rs.getString("rol");
        if (Usuario.Rol.ADMINISTRADOR.name().equals(rol)) {
            return new Administrador(rs.getString("id"), rs.getString("clave"));
        }
        return new Funcionario(rs.getString("id"), rs.getString("clave"),
                rs.getString("nombre"), rs.getString("telefono"));
    }
}
